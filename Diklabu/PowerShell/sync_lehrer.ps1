﻿<#
.Synopsis
   Synchronisieren der Lehrer mit der AD
.DESCRIPTION
   Synchronisieren der Lehrer mit der AD
.EXAMPLE
   Sync-Teachers
#>
function Sync-Teachers
{
    Param
    (
         [switch]$whatif,
        [switch]$force
    )

    Begin
    {
        $config=Get-Content "$PSScriptRoot/config.json" | ConvertFrom-json
        $password = $config.bindpassword | ConvertTo-SecureString -asPlainText -Force
        $credentials = New-Object System.Management.Automation.PSCredential -ArgumentList "ldap-user", $password
        $u=Get-ADGroupMember -Identity Lehrer -Server 172.31.0.1 -Credential $credentials | Get-ADUser -Properties Mail,Initials -Server 172.31.0.1 -Credential $credentials
        foreach ($user in $u) {
            if ($user.Initials) {
                Write-Output  "Bearbeite $($user.GivenName) $($user.Name) ($($user.Initials))" 
                Write-Host  "Bearbeite $($user.GivenName) $($user.Name) ($($user.Initials))" 
                
                $t=Get-Teacher -ID $user.Initials
                if ($t) {
                    Write-Host "Lehrer gefunden aktualisiere Daten" -BackgroundColor DarkGreen
                    Write-Output "+- Lehrer gefunden aktualisiere Daten" 
                    if (-not $whatif) {
                        Set-Teacher -ID $user.Initials -VNAME $user.GivenName -NNAME $user.Name -EMAIL $user.Mail
                    }
                }
                else {
                    Write-Host "Neuer Lehrer, wird angelegt!" -BackgroundColor DarkRed
                    Write-Output "+- Neuer Lehrer, wird angelegt!" 
                    if (-not $force) {
                        $r=Read-Host "Neuen Lehrer anlegen (j/n)"
                        if ($r -eq "j") {
                           if (-not $whatif) {
                                New-Teacher -ID $user.Initials -VNAME $user.GivenName -NNAME $user.Name -EMAIL $user.Mail
                            }
                        }
                    }
                    if (-not $whatif) {
                        New-Teacher -ID $user.Initials -VNAME $user.GivenName -NNAME $user.Name -EMAIL $user.Mail
                    }
                }
            }
            else {
                Write-Host "Achtung der Lehrer "$user.GivenName" "$user.Name" hat keine Initialen (Kürzel) und kann nicht synchronisiert werden" -BackgroundColor DarkRed
                Write-Output "#Achtung der Lehrer $($user.GivenName) $($user.Name) hat keine Initialen (Kürzel) und kann nicht synchronisiert werden" 
            }    
        }
        $teachers = Get-Teachers
        foreach ($t in $teachers) {
            $found=$false;
            foreach ($user in $u) {
                if ($t.id -eq $user.Initials) {
                    $found=$true;
                    break;
                }
            }
            if (-not $found) {
                Write-Host "Achtung der Lehrer mit Initialen "$t.ID" ("$t.VNAME" "$t.NNAME") wurde nicht gefunden" -BackgroundColor DarkRed
                Write-Output "Achtung der Lehrer mit Initialen $($t.ID) ($($t.VNAME) $($t.NNAME)) wurde nicht gefunden" 
                if (-not $force) {
                    $r=Read-Host "Soll der Lehrer aus dem Klassenbuch entfernt werden? (j/n)"
                    if ($r -eq "j") {
                        if (-not $whatif) {
                            $r=Delete-Teacher -ID $t.ID
                        }
                        Write-Host "Lehrer "$t.ID" gelöscht!" -BackgroundColor DarkGreen
                        Write-Output "+- Lehrer $($t.ID) gelöscht!" 
                    }
                }
                else {
                    if (-not $whatif) {
                        $r=Delete-Teacher -ID $t.ID
                    }
                    Write-Host "Lehrer "$t.ID" gelöscht!" -BackgroundColor DarkGreen
                    Write-Output "+- Lehrer $($t.ID) gelöscht!" 
                }
            }
        }
    }
}
