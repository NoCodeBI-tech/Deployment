# Get Parameter when run this file
param(
    [Parameter(Mandatory=$true)]
    [string]$URL
)

# Update hosts file
$hostsFile = "$env:WINDIR\System32\drivers\etc\hosts"
$entry = "127.0.0.1 $URL"
$hostsContent = Get-Content $hostsFile

if ($hostsContent -notcontains $entry) {
    Add-Content -Path $hostsFile -Value $entry
    Write-Host "`n🌐 Added entry to hosts file: $entry" -ForegroundColor Green
} else {
    Write-Host "`nℹ️ Hosts entry already exists: $entry" -ForegroundColor Yellow
}

# Run this like
# powershell -ExecutionPolicy Bypass -File update-hosts.ps1 -URL "local-dev.nocodebi.io"