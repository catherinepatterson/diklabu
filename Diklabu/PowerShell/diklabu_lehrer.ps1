﻿<#
    VERBEN:
        get .... findet einen Lehrer durch angabe des PK (Kürzel)
        set .... ändert Attribute eines Lehrers durch Angabe des PK (Kürzel)
        new .... erzeugt ein neuen Lehrer
        delete . einen Lehrer löschen

    NOMEN:
        teacher
#>


<#
.Synopsis
   Attribute eines oder mehrerer Lehrer ändern
.DESCRIPTION
   Ändert die Attribute eines oder mehrerer Lehrer
.EXAMPLE
   Set-Teacher -ID TU -EMAIL "jtuttas@gmx.net"
.EXAMPLE
   Set-Teacher -ID TU -NName "Dr. Tuttas" -VNAME "Jörg"
.EXAMPLE
   "TU","BK"| Set-Teacher -VNAME "NN"

#>
function Set-Teacher
{
    Param
    (
        [Parameter(Mandatory=$true,ValueFromPipeline=$true,ValueFromPipelineByPropertyName=$true,Position=0)]
        [String]$ID,

        # Adresse des Diklabu Servers
        [String]$uri=$global:server,

        #Nachname des Lehrers
        [String]$NNAME,
        #Vorname des Lehrers
        [String]$VNAME,
        #EMAIL Adresse des Lehrers
        [String]$EMAIL,
        #TELEFON des Lehrers
        [String]$TELEFON,
        [switch]$whatif
    )

    Begin
    {
        $headers=@{}
        $headers["content-Type"]="application/json;charset=iso-8859-1"
        $headers["auth_token"]=$global:auth_token;
    }
    Process
    {
        $lehrer=echo "" | Select-Object -Property "NNAME","VNAME","TELEFON","EMAIL"
        if ($NNAME) {
            $lehrer.NNAME=$NNAME
        }
        if ($VNAME) {
            $lehrer.VNAME=$VNAME
        }
        if ($EMAIL) {
            $lehrer.EMAIL=$EMAIL
        }
        if ($TELEFON) {
            $lehrer.TELEFON=$TELEFON
        }
        try {
            if (-not $whatif) {
                $r=Invoke-RestMethod -Method Post -Uri ($uri+"lehrer/admin/id/"+$ID) -Headers $headers -Body (ConvertTo-Json $lehrer)
            }
            Write-Verbose "Ändere die Daten des Lehrer mit der ID $id auf $lehrer"
          return $r;
         } catch {
            Write-Error "Set-Teacher: Status-Code"$_.Exception.Response.StatusCode.value__ " "$_.Exception.Response.StatusDescription 
        }

    }
    End
    {
    }
}


<#
.Synopsis
   Einen odere mehrere Lehrer abfragen
.DESCRIPTION
   Fragt einen oder mehrere Lehrer über ID (Kürzel) ab
.EXAMPLE
   Get-Teacher -ID "TU"
.EXAMPLE
   Get-Teacher -ID "TU" -uri http://localhost:8080/Diklabu/api/v1/
.EXAMPLE
   "TU","BK" | Get-Teacher

#>
function Get-Teacher
{
    Param
    (
        [Parameter(Mandatory=$true,ValueFromPipeline=$true,ValueFromPipelineByPropertyName=$true,Position=0)]
        [String]$ID,

        # Adresse des Diklabu Servers
        [String]$uri=$global:server
    )

    Begin
    {
          $headers=@{}
          $headers["content-Type"]="application/json;charset=iso-8859-1"
          $headers["auth_token"]=$global:auth_token;
    }
    Process
    {
        try {
            $r=Invoke-RestMethod -Method Get -Uri ($uri+"lehrer/"+$ID) -Headers $headers 
            Write-Verbose "Abfrage der Daten des Lehrer mit der ID $ID"
            return $r;
        } catch {
            Write-Error "Get-Teacher: Status-Code"$_.Exception.Response.StatusCode.value__ " "$_.Exception.Response.StatusDescription 
        }
    }
}

<#
.Synopsis
   Alle Lehrer abfragen
.DESCRIPTION
   Fragt alle Lehrer ab
.EXAMPLE
   Get-Teachers 
.EXAMPLE
   Get-Teachers uri http://localhost:8080/Diklabu/api/v1/
#>
function Get-Teachers
{
    Param
    (

        # Adresse des Diklabu Servers
        [String]$uri=$global:server
    )

    Begin
    {
        try {
            $r=Invoke-RestMethod -Method Get -Uri ($uri+"noauth/lehrer/") -Headers $headers 
            Write-Verbose "Abfrage aller Lehrer"
            return $r;
        } catch {
            Write-Error "Get-Teachers: Status-Code"$_.Exception.Response.StatusCode.value__ " "$_.Exception.Response.StatusDescription 
        }
    }
}


<#
.Synopsis
   Einen neuen oder mehrere neu Lehrer anlegen
.DESCRIPTION
   Erzeugt einen Neuen Lehrer. Bzw. importiert die Betriebe aus einer CSV Datei mit folgenden Einträgen
   "NNAME","VNAME","TELEFON","EMAIL","ID"
.EXAMPLE
   New-Teacher -ID "NM"
.EXAMPLE
   New-Teacher -ID ""NM" -NNAME "Neumann"
.EXAMPLE
   Import-Csv lehrer.csv | New-Teacher

#>
function New-Teacher
{
    Param
    (
        # ID (Kürzel des Lehrers)
        [Parameter(Mandatory=$true,ValueFromPipeline=$true,Position=0,ValueFromPipelineByPropertyName=$true)]
        [String]$ID,

        # Adresse des Diklabu Servers
        [String]$uri=$global:server,

        #Nachname des Lehrers
        [Parameter(ValueFromPipelineByPropertyName=$true)]
        [String]$NNAME,
        #Vorname des Lehrers
        [Parameter(ValueFromPipelineByPropertyName=$true)]
        [String]$VNAME,
        # Telefon des Lehrers
        [Parameter(ValueFromPipelineByPropertyName=$true)]
        [String]$TELEFON,
        #Email Adresse des Lehrers
        [Parameter(ValueFromPipelineByPropertyName=$true)]
        [String]$EMAIL,
        [switch]$whatif

    )

    Begin
    {
        $headers=@{}
        $headers["content-Type"]="application/json;charset=iso-8859-1"
        $headers["auth_token"]=$global:auth_token;
    }
    Process
    {
        $lehrer=echo "" | Select-Object -Property "id","NNAME","VNAME","TELEFON","EMAIL"
        $lehrer.NNAME=$NNAME
        $lehrer.VNAME=$VNAME
        $lehrer.TELEFON=$TELEFON
        $lehrer.EMAIL=$EMAIL
        $lehrer.id=$ID
        try {
            if (-not $whatif) {
                $r=Invoke-RestMethod -Method Post -Uri ($uri+"lehrer/admin/") -Headers $headers -Body (ConvertTo-Json $lehrer)
            }
            Write-Verbose "Lege neuen Lehrer an mit den Daten $lehrer"
          return $r;
        } catch {
            Write-Error "New-Lehrer: Status-Code"$_.Exception.Response.StatusCode.value__ " "$_.Exception.Response.StatusDescription 
        }
    }
   
}
<#
.Synopsis
   Einen oder mehrere Lehrer(e) löschen
.DESCRIPTION
   Löscht einen oder mehrere Lehrer in der Tabelle LEHRER
.EXAMPLE
   Delete-Teacher -ID "NM"
.EXAMPLE
   Delete-Teacher -ID "NM" -uri http://localhost:8080/Diklabu/api/v1/
.EXAMPLE
   "NN","NM" | Delete-Teacher

#>
function Delete-Teacher
{
    Param
    (
        [Parameter(Mandatory=$true,ValueFromPipeline=$true,ValueFromPipelineByPropertyName=$true,Position=0)]
        [String]$ID,

        # Adresse des Diklabu Servers
        [String]$uri=$global:server,
        [switch]$whatif
    )

    Begin
    {
            $headers=@{}
            $headers["content-Type"]="application/json;charset=iso-8859-1"
            $headers["auth_token"]=$global:auth_token;
    }
    Process
    {
        try {
            if (-not $whatif) {
                $r=Invoke-RestMethod -Method Delete -Uri ($uri+"lehrer/admin/"+$ID) -Headers $headers 
            }
            Write-Verbose "Lösche Lehrer mit der ID $ID"
          return $r;
          } catch {
            Write-Error "Delete-Teacher: Status-Code"$_.Exception.Response.StatusCode.value__ " "$_.Exception.Response.StatusDescription 
        }
    }
}
