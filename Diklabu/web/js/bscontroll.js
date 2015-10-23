
var courseList;
var wuensche = new Array(3);

$("#version").text(VERSION);
getCourseList();

$("#btnWaehlen").click(function () {
    if ($("#name").val() == "" || $("#vorName").val() == "" || $("#gebDatum").val() == "") {
        toastr["info"]("Bitte die Anmeldedaten vollständig ausfüllen!", "Information");
    }
    else {
        if (wuensche[0] == undefined || wuensche[1] == undefined || wuensche[2] == undefined) {
            toastr["info"]("Bitte drei Kurse Wünschen!", "Information");
        }
        else {
            console.log("Wunsche=" + JSON.stringify(wuensche));
            var credentials = {
                name: $("#name").val(),
                vorName: $("#vorName").val(),
                gebDatum: $("#gebDatum").val()
            };
            var ticketing = {
                credential: credentials,
                courseList: wuensche
            };
            console.log("ticketing=" + JSON.stringify(ticketing));
            $.ajax({
                url: SERVER + "/Diklabu/api/v1/courseselect/booking",
                type: "POST",
                contentType: "application/json; charset=UTF-8",
                dataType: "json",
                data: JSON.stringify(ticketing),
                success: function (data) {
                    console.log("buchen finished" + JSON.stringify(data));
                    if (data.credential.login == false) {
                        toastr["error"](data.msg, "Fehler!");
                    }
                    else {
                        if (data.success == true) {
                            toastr["success"](data.msg, "OK!");
                            $("#name").val("");
                            $("#vorName").val("");
                            $("#gebDatum").val("");
                            $("li").removeClass("disabled");
                            $("#erstWunsch").text("kein Kurs gewählt");
                            $("#zweitWunsch").text("kein Kurs gewählt");
                            $("#drittWunsch").text("kein Kurs gewählt");
                        }
                        else {
                            toastr["error"](data.msg, "Fehler!");
                        }
                    }
                }
            });
        }
    }
});



function getCourseList() {
    $.ajax({
        url: SERVER + "/Diklabu/api/v1/courseselect/booking",
        type: "GET",
        contentType: "application/json; charset=UTF-8",
        dataType: "json",
        error: function () {
            toastr["error"]("Kann Kursliste nicht vom Server laden!", "Fehler!");
        },
        success: function (data) {
            console.log("receive Course List" + JSON.stringify(data));
            courseList = data;
            wuensche = new Array(3);
            $("#erstWunschDropdown").empty();
            $("#zweitWunschDropdown").empty();
            $("#drittWunschDropdown").empty();
            for (var i = 0; i < courseList.length; i++) {
                $("#erstWunschDropdown").append('<li  id="w1' + i + '" role="presentation" ><a kursid="' + i + '" role="menuitem" tabindex="-1" href="#f">' + courseList[i].TITEL + '</a></li>')
                $("#zweitWunschDropdown").append('<li  id="w2' + i + '" role="presentation" ><a kursid="' + i + '" role="menuitem" tabindex="-1" href="#f">' + courseList[i].TITEL + '</a></li>')
                $("#drittWunschDropdown").append('<li  id="w3' + i + '" role="presentation" ><a kursid="' + i + '"role="menuitem" tabindex="-1" href="#f">' + courseList[i].TITEL + '</a></li>')
            }
            $('#erstWunschDropdown li > a').click(function (e) {
                var index = $(this).attr('kursid');
                console.log("index = "+index+" class="+$("#w1" + index).attr("class"));
                if ($("#w1" + index).attr("class") != "disabled") {
                    $('#erstWunsch').text(this.innerHTML);
                    if (wuensche[0]!=undefined) {
                        console.log("Es existierte bereits ein Erstwunsch mit id="+wuensche[0].id);
                        var i=findWunschbyId(wuensche[0].id);
                        console.log("Dieser hat den index "+i);
                        $("#w1"+i).removeClass("disabled");
                        $("#w2"+i).removeClass("disabled");
                        $("#w3"+i).removeClass("disabled");
                    }
                    var course = courseList[index];
                    console.log("Gewählt wurde:" + course.TITEL);
                    wuensche[0] = course;
                    $("#w1" + index).addClass("disabled");
                    $("#w2" + index).addClass("disabled");
                    $("#w3" + index).addClass("disabled");
                }
            });
            $('#zweitWunschDropdown li > a').click(function (e) {
                var id = $(this).attr('kursid');
                if ($("#w2" + id).attr("class") != "disabled") {
                    $('#zweitWunsch').text(this.innerHTML);
                    if (wuensche[1]!=undefined) {
                        console.log("Es existierte bereits ein Zweitwunsch mit id="+wuensche[1].id);
                        var i=findWunschbyId(wuensche[1].id);
                        console.log("Dieser hat den index "+i);
                        $("#w1"+i).removeClass("disabled");
                        $("#w2"+i).removeClass("disabled");
                        $("#w3"+i).removeClass("disabled");
                    }
                    var course = courseList[id];
                    console.log("Gewählt wurde:" + course.TITEL);
                    wuensche[1] = course;
                    $("#w1" + id).addClass("disabled");
                    $("#w2" + id).addClass("disabled");
                    $("#w3" + id).addClass("disabled");
                }
            });
            $('#drittWunschDropdown li > a').click(function (e) {
                var id = $(this).attr('kursid');
                if ($("#w3" + id).attr("class") != "disabled") {
                    $('#drittWunsch').text(this.innerHTML);
                    if (wuensche[2]!=undefined) {
                        console.log("Es existierte bereits ein Drittwunsch mit id="+wuensche[2].id);
                        var i=findWunschbyId(wuensche[2].id);
                        console.log("Dieser hat den index "+i);
                        $("#w1"+i).removeClass("disabled");
                        $("#w2"+i).removeClass("disabled");
                        $("#w3"+i).removeClass("disabled");
                    }
                    var course = courseList[id];
                    console.log("Gewählt wurde:" + course.TITEL);
                    wuensche[2] = course;
                    $("#w1" + id).addClass("disabled");
                    $("#w2" + id).addClass("disabled");
                    $("#w3" + id).addClass("disabled");
                }
            });

        }
    });



}

/**
 * An welcher Stelle der coursliste findet sich welcher Kurs (ID)
 * @param {type} id die Id des Kurses
 * @returns {Number|j} die Position in der Kursliste
 */
function findWunschbyId( id) {
    for ( j=0;j<courseList.length;j++) {
        if (courseList[j].id==id) {
            return j;
        }        
    }
}

toastr.options = {
    "closeButton": false,
    "debug": false,
    "newestOnTop": false,
    "progressBar": false,
    "positionClass": "toast-top-right",
    "preventDuplicates": false,
    "onclick": null,
    "showDuration": "300",
    "hideDuration": "1000",
    "timeOut": "5000",
    "extendedTimeOut": "1000",
    "showEasing": "swing",
    "hideEasing": "linear",
    "showMethod": "fadeIn",
    "hideMethod": "fadeOut"
}