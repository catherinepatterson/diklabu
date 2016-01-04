// ID der gewählten Klasse
var idKlasse;
var nameKlasse;
// Schueler der gewählten Klasse
var schueler;
// Anwesenheit der gewählten Klasse und des gewählten Zeitraums
var anwesenheit;

var inputVisible = false;

$("#eintragDatum").datepicker("setDate", "+0");
var today = new Date();
$("#startDate").datepicker("setDate", new Date(today.getFullYear(), today.getMonth(), today.getDate() - 7));
$("#endDate").datepicker("setDate", new Date());

console.log("found token:" + sessionStorage.auth_token);
if (sessionStorage.auth_token != undefined && sessionStorage.auth_token != "undefined") {
    console.log("Build gui for logged in user");
    loggedIn();
}
$("#login").click(function () {
    if (sessionStorage.auth_token == undefined || sessionStorage.auth_token == "undefined") {
        var myData = {
            "benutzer": $('#lehrer').find(":selected").attr("idplain"),
            "kennwort": $("#kennwort").val()
        };
        console.log("idplain = " + $('#lehrer').find(":selected").attr("idplain"));
        sessionStorage.service_key = $('#lehrer').find(":selected").attr("idplain") + "f80ebc87-ad5c-4b29-9366-5359768df5a1";
        console.log("Service key =" + sessionStorage.service_key);

        $.ajax({
            cache: false,
            contentType: "application/json; charset=UTF-8",
            headers: {
                "service_key": sessionStorage.service_key
            },
            dataType: "json",
            url: "/Diklabu/api/v1/auth/login/",
            type: "POST",
            data: JSON.stringify(myData),
            success: function (jsonObj, textStatus, xhr) {
                sessionStorage.auth_token = jsonObj.auth_token;
                console.log("Thoken = " + jsonObj.auth_token);

                toastr["success"]("Login erfolgreich", "Info!");
                sessionStorage.myselfplain = $('#lehrer').find(":selected").attr("idplain");
                sessionStorage.myself = $('#lehrer').val();
                loggedIn();
                nameKlasse = $("#klassen").val();
                refreshVerlauf(nameKlasse);
                refreshKlassenliste(nameKlasse);
            },
            error: function (xhr, textStatus, errorThrown) {
                toastr["error"]("Login fehlgeschlagen", "Fehler!");
                console.log("HTTP Status: " + xhr.status);
                console.log("Error textStatus: " + textStatus);
                console.log("Error thrown: " + errorThrown);
            }
        });
    }
    else {
        var myData = {
            "benutzer": $("#lehrer").val(),
            "kennwort": $("#kennwort").val()
        };

        $.ajax({
            cache: false,
            contentType: "application/json; charset=UTF-8",
            headers: {
                "service_key": sessionStorage.service_key,
                "auth_token": sessionStorage.auth_token
            },
            dataType: "json",
            url: "/Diklabu/api/v1/auth/logout/",
            type: "POST",
            data: JSON.stringify(myData),
            success: function (jsonObj, textStatus, xhr) {
                sessionStorage.auth_token = undefined;
                toastr["success"]("Logout erfolgreich", "Info!");
                sessionStorage.myself = undefined;
                loggedOut();
            },
            error: function (xhr, textStatus, errorThrown) {
                toastr["error"]("Logout fehlgeschlagen!Status Code=" + xhr.status, "Fehler!");
                console.log("HTTP Status: " + xhr.status);
                console.log("Error textStatus: " + textStatus);
                console.log("Error thrown: " + errorThrown);
                if (xhr.status = 401) {
                    loggedOut();
                }
            }
        });

    }
});


$.ajax({
    url: SERVER + "/Diklabu/api/v1/noauth/klassen",
    type: "GET",
    contentType: "application/json; charset=UTF-8",
    success: function (data) {
        $("#klassen").empty();
        for (i = 0; i < data.length; i++) {
            $("#klassen").append("<option dbid='" + data[i].id + "'>" + data[i].KNAME + "</option>");
        }
        nameKlasse = data[0].KNAME;
        idKlasse = data[0].id;
        $("#idklasse").val(idKlasse);
        if (sessionStorage.auth_token != undefined && sessionStorage.auth_token != "undefined") {
            refreshVerlauf(nameKlasse);
            refreshKlassenliste(nameKlasse)
        }
    },
    error: function () {
        toastr["error"]("kann Klassenliste nicht vom Server laden", "Fehler!");
    }
});
$.ajax({
    url: SERVER + "/Diklabu/api/v1/noauth/lernfelder",
    type: "GET",
    contentType: "application/json; charset=UTF-8",
    success: function (data) {
        $("#lernfelder").empty();
        for (i = 0; i < data.length; i++) {
            if (data[i].BEZEICHNUNG != undefined)
                $("#lernfelder").append("<option dbid='" + data[i].id + "'>" + data[i].BEZEICHNUNG + "</option>");
        }
    },
    error: function () {
        toastr["error"]("kann Lernfelder nicht vom Server laden", "Fehler!");
    }
});
$.ajax({
    url: SERVER + "/Diklabu/api/v1/noauth/lehrer",
    type: "GET",
    contentType: "application/json; charset=UTF-8",
    success: function (data) {
        $("#lehrer").empty();
        for (i = 0; i < data.length; i++) {
            $("#lehrer").append("<option idplain=" + data[i].idplain + ">" + data[i].id + "</option>");
        }
    },
    error: function () {
        toastr["error"]("kann Lehrerliste nicht vom Server laden", "Fehler!");
    }
});

function refreshVerlauf(kl) {
    console.log("Refresh Verlauf f. Klasse " + kl + " von " + $("#startDate").val() + " bis " + $("#endDate").val());
    $.ajax({
        url: SERVER + "/Diklabu/api/v1/verlauf/" + kl + "/" + $("#startDate").val() + "/" + $("#endDate").val(),
        type: "GET",
        cache: false,
        headers: {
            "service_key": sessionStorage.service_key,
            "auth_token": sessionStorage.auth_token
        },
        contentType: "application/json; charset=UTF-8",
        success: function (data) {
            verlauf = data;
            $("#tabelleVerlauf").empty();
            for (i = 0; i < data.length; i++) {
                var datum = data[i].wochentag + " " + data[i].DATUM;
                datum = datum.substring(0, datum.indexOf('T'));
                if (data[i].ID_LEHRER == sessionStorage.myself) {
                    $("#tabelleVerlauf").append("<tr dbid='" + data[i].ID + "' index='" + i + "' class='success'><td>" + datum + "</td><td>" + data[i].ID_LEHRER + "</td><td>" + data[i].ID_LERNFELD + "</td><td>" + data[i].STUNDE + "</td><td>" + data[i].INHALT + "</td><td>" + data[i].BEMERKUNG + "</td><td>" + data[i].AUFGABE + "</td></tr>");
                }
                else {
                    $("#tabelleVerlauf").append("<tr dbid='" + data[i].ID + "' index='" + i + "' ><td>" + datum + "</td><td>" + data[i].ID_LEHRER + "</td><td>" + data[i].ID_LERNFELD + "</td><td>" + data[i].STUNDE + "</td><td>" + data[i].INHALT + "</td><td>" + data[i].BEMERKUNG + "</td><td>" + data[i].AUFGABE + "</td></tr>");
                }
            }
            $(".success").click(function () {
                console.log("Auswahl Verlaufseintrag ID=" + $(this).attr("dbid"));
                idVerlauf = $(this).attr("dbid");
                index = $(this).attr("index");
                $("#lernsituationVerlauf").val(verlauf[index].AUFGABE);
                $("#bemerkungVerlauf").val(verlauf[index].BEMERKUNG);
                $("#inhaltVerlauf").val(verlauf[index].INHALT);
                var dat = verlauf[index].DATUM;
                dat = dat.substring(0, dat.indexOf('T'));
                $("#eintragDatum").val(dat);
                var lf = verlauf[index].ID_LERNFELD;
                lf = lf.trim();
                console.log("Setze Lernfeld auf (" + lf + ")");
                $("#lernfelder").val(lf);
                $("#stunde").val(verlauf[index].STUNDE);
                $("#updateVerlauf").removeClass("disabled");
                $("#deleteVerlauf").removeClass("disabled");
            });
        },
        error: function (xhr, textStatus, errorThrown) {
            toastr["error"]("kann Verlauf nicht vom Server laden! Status Code=" + xhr.status, "Fehler!");
        }
    });
}

function refreshKlassenliste(kl) {
    console.log("Refresh Klassenliste f. Klasse " + kl);
    $.ajax({
        url: SERVER + "/Diklabu/api/v1/klasse/" + kl,
        type: "GET",
        cache: false,
        headers: {
            "service_key": sessionStorage.service_key,
            "auth_token": sessionStorage.auth_token
        },
        contentType: "application/json; charset=UTF-8",
        success: function (data) {
            schueler = data;
            $("#tabelleKlasse").empty();

            $("#tabelleKlasse").append('<thead><tr><th >Name</th></tr></thead>');
            $("#tabelleKlasse").append("<tbody>");
            for (i = 0; i < data.length; i++) {
                $("#tabelleKlasse").append('<tr><td><img src="img/Info.png" id="S' + data[i].id + '" class="infoIcon"> ' + data[i].VNAME + " " + data[i].NNAME + '</td></tr>');
            }
            $("#tabelleKlasse").append("</tbody>");
            refreshAnwesenheit(kl);
        },
        error: function (xhr, textStatus, errorThrown) {
            toastr["error"]("kann Schüler der Klasse " + kl + " nicht vom Server laden! Status Code=" + xhr.status, "Fehler!");
        }
    });
}

function refreshAnwesenheit(kl) {
    console.log("Refresh Anwesenheit f. Klasse " + kl + " von " + $("#startDate").val() + " bis " + $("#endDate").val());
    var url = SERVER + "/Diklabu/api/v1/anwesenheit/" + kl + "/" + $("#startDate").val() + "/" + $("#endDate").val();
    console.log("URL=" + url);
    $.ajax({
        // anwesenheit/FISI13A/2015-09-08/2015-09-15
        url: SERVER + "/Diklabu/api/v1/anwesenheit/" + kl + "/" + $("#startDate").val() + "/" + $("#endDate").val(),
        type: "GET",
        cache: false,
        headers: {
            "service_key": sessionStorage.service_key,
            "auth_token": sessionStorage.auth_token
        },
        contentType: "application/json; charset=UTF-8",
        success: function (data) {
            anwesenheit = data;
            console.log("anwesenheit=" + JSON.stringify(data));
            $("#tabelleAnwesenheit").empty();
            var dateStart = new Date($("#startDate").val());
            var dateEnde = new Date($("#endDate").val());
            var current = new Date(dateStart);
            console.log("startDate=" + dateStart + " endeDate=" + dateEnde);
            // Leere Anwesenheitstabelle erzeugen
            var tab = "";
            tab += '<thead><tr>';
            while (current <= dateEnde) {
                tab += '<th>&nbsp; ' + current.getDate() + "." + (current.getMonth() + 1) + "." + current.getFullYear() + '&nbsp; </th>';
                current.setDate(current.getDate() + 1);
            }
            tab += '</tr></thead>';
            $("#tabelleAnwesenheit").append(tab);
            $("#tabelleAnwesenheit").append('<tbody>');
            generateAnwesenheitsTable();
            $("#tabelleAnwesenheit").append('</tbody>');
            // Daten in die Tabelle Eintragen
            for (var i = 0; i < anwesenheit.length; i++) {
                var eintraege = anwesenheit[i].eintraege;
                for (var j = 0; j < eintraege.length; j++) {
                    var dat = eintraege[j].DATUM;
                    dat = dat.substring(0, dat.indexOf("T"));
                    var id = eintraege[j].ID_SCHUELER + "_" + dat;
                    console.log("suche html id " + id);
                    $("#" + id).text(eintraege[j].VERMERK);
                    $("#" + id).attr("id_lehrer", eintraege[j].ID_LEHRER);
                    $("#" + id).addClass("anwesenheitsPopup");
                }
            }
            // Eventhandler auf einen anwesenheitseintrag
            $(".anwesenheitsPopup").hover(function () {
                //alert("popup anzeigen");
            }, function () {

            });


        },
        error: function (xhr, textStatus, errorThrown) {
            toastr["error"]("kann Anwesenheit der Klasse " + kl + " nicht vom Server laden! Status Code=" + xhr.status, "Fehler!");
        }
    });
}

var oldText="";
function generateAnwesenheitsTable() {
    var dateStart = new Date($("#startDate").val());
    var dateEnde = new Date($("#endDate").val());
    var tab = "";
    console.log("startDate=" + dateStart + " endeDate=" + dateEnde);
    for (var i = 0; i < schueler.length; i++) {
        tab += "<tr>";
        var dateStart = new Date($("#startDate").val());
        while (dateStart <= dateEnde) {
            tab += "<td class=\"anwesenheit\" align=\"center\" index=\"" + i + "\" id=\"" + schueler[i].id + "_" + toSQLString(dateStart) + "\" >&nbsp;</td>";
            dateStart.setDate(dateStart.getDate() + 1);
        }
        tab += '</tr>';
    }
    $("#tabelleAnwesenheit").append(tab);
    $(".anwesenheit").click(function () {
        console.log("input Visible=" + inputVisible);
        oldText=$(this).text();
        if (!inputVisible) {
            inputTd = $(this);
            inputVisible = true;
            $('body').off('keydown', "#anwesenheitsInput");
            var t = $(this).text();
            $(this).empty();
            $(this).append('<input class="inputAnwesenheit" id="anwesenheitsInput" maxlength="12" size="4" type="text" value="' + t + '"></input>');
            $("#anwesenheitsInput").focus();
            $("#anwesenheitsInput")[0].setSelectionRange(t.length - 1, t.length - 1);
            $('body').on('keydown', "#anwesenheitsInput", handelKeyEvents);
        }
        else {
            inputVisible = true;
            $('body').off('keydown', "#anwesenheitsInput");
            var txt = $("#anwesenheitsInput").val();
            console.log("eingegeben wurde " + txt);
            $("#anwesenheitsInput").remove();
            inputTd.text(txt);
            inputTd = $(this);
            var t = $(this).text();
            $(this).empty();
            $(this).append('<input class="inputAnwesenheit" id="anwesenheitsInput" maxlength="12" size="4" type="text" value="' + t + '"></input>');
            $("#anwesenheitsInput").focus();
            $("#anwesenheitsInput")[0].setSelectionRange(t.length - 1, t.length - 1);
            $('body').on('keydown', "#anwesenheitsInput", handelKeyEvents);
        }
    });

}

/**
 * Je nach eingaben in der Tabelle werden andere Elemente aktiviert bzw. die 
 * Daten zum Server übertrageb (z.B. bei TAB)
 * @returns {undefined}
 */
function handelKeyEvents(e) {
    var keyCode = e.keyCode || e.which;
    //console.log("key Pressed" + keyCode);
    if (keyCode == 13 || keyCode == 9 || keyCode==27) {
        var txt = $(this).val();
        console.log("eingegeben wurde " + txt);
        $(this).remove();
        if (keyCode!=27) {
            inputTd.text(txt);
            anwesenheitsEintrag(inputTd,txt);
        }
        else {
            inputTd.text(oldText);
        }
        inputVisible = false;
        var index = inputTd.index();
        var tr = inputTd.parent();
        
        console.log("index=" + index);
        console.log("Nachbar-Element hat Text " + $(inputTd).next().text());
        console.log("Vorheriges-Element hat Text " + $(inputTd).prev().text());
        console.log("Oberhalb hat den Wert " + tr.prev().find('td').eq(index).text());
        console.log("Unterhalb hat den Wert " + tr.next().find('td').eq(index).text());
        // unteres Element auswählen
        if (keyCode == 13) {
            // nicht letzte Zeile
            if (tr.next().index() != -1) {
                inputVisible = true;
                inputTd = tr.next().find('td').eq(index);
                t = inputTd.text();
                inputTd.empty();
                inputTd.append('<input class="inputAnwesenheit" id="anwesenheitsInput" maxlength="12" size="4" type="text" value="' + t + '"></input>');
                $("#anwesenheitsInput").focus();
            }
            else {
                $('body').off('keydown', "#anwesenheitsInput");
            }
        }
        // bei TAB oder ESC
        if (keyCode == 9 || keyCode == 27) {
            $('body').off('keydown', "#anwesenheitsInput");
        }
    }
}

function anwesenheitsEintrag(td,txt) {
    var id = td.attr("id");
    var dat=id.substring(id.indexOf("_")+1);
    var sid=id.substring(0,id.indexOf("_"));
    var eintr = {
        "DATUM": dat+"T00:00:00",
        "ID_LEHRER": sessionStorage.myself,
        "ID_SCHUELER": parseInt(sid),
        "VERMERK": txt
    };
    
    console.log ("Sende zum Server:"+JSON.stringify(eintr));
    $.ajax({
        url: SERVER + "/Diklabu/api/v1/anwesenheit/",
        type: "POST",
        cache: false,
        data: JSON.stringify(eintr),
        headers: {
            "service_key": sessionStorage.service_key,
            "auth_token": sessionStorage.auth_token
        },
        contentType: "application/json; charset=UTF-8",
        success: function (data) {
        },
        error: function (xhr, textStatus, errorThrown) {
            toastr["error"]("kann Anwesenheitseintrag nicht zum Server senden! Status Code=" + xhr.status, "Fehler!");
        }
    });
}

function toSQLString(d) {
    var s = "";
    s += d.getFullYear() + "-" + (d.getMonth() + 1) + "-" + d.getDate();
    return s;
}

function loggedOut() {
    $("#kuerzelContainer").show();
    $("#kennwortContainer").show();
    $("#login").removeClass("btn-danger");
    $("#login").addClass("btn-success");
    $("#login").text("Login");
    $("#dokumentation").addClass("disabled");
    $("#tabelle").hide();
    $("#inputVerlaufContainer").hide();
    $("#klassen").unbind();
    $("#dokumentation").unbind();
    $('#startDate').datepicker().off('changeDate');
    $('#endDate').datepicker().off('changeDate');
    sessionStorage.auth_token = undefined;
    sessionStorage.myself = undefined;
}
function loggedIn() {
    $("#kuerzelContainer").hide();
    $("#kennwortContainer").hide();
    $("#login").removeClass("btn-success");
    $("#login").addClass("btn-danger");
    $("#login").text("Logout " + sessionStorage.myself);
    $("#dokumentation").removeClass("disabled");
    $("#tabelle").show();
    $("#inputVerlaufContainer").show();
    $('#startDate').datepicker().on('changeDate', function (ev) {
        refreshVerlauf($("#klassen").val());
        refreshAnwesenheit($("#klassen").val());
        $("#from").val($("#startDate").val());

    });
    $('#endDate').datepicker().on('changeDate', function (ev) {
        refreshVerlauf($("#klassen").val());
        refreshAnwesenheit($("#klassen").val());
        $("#to").val($("#endDate").val());
    });
    $("#klassen").change(function () {
        console.log("Klasse ID=" + $('option:selected', this).attr('dbid') + " KNAME=" + $("#klassen").val());
        idKlasse = $('option:selected', this).attr('dbid');
        refreshVerlauf($("#klassen").val());
        refreshKlassenliste($("#klassen").val());
        $("#idklasse").val(idKlasse);

    });
    $("#auth_token").val(sessionStorage.auth_token);
    $("#service_key").val(sessionStorage.service_key);
    $("#idklasse").val(idKlasse);
    $("#from").val($("#startDate").val());
    $("#to").val($("#endDate").val());
}