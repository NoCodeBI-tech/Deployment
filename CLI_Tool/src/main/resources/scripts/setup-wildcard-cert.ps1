# ================================
# Wildcard Certificate Installer
# For *.abc.com - by ChatGPT
# ================================

$domain = "*.nocodebi.io"
$commonName = "*.nocodebi.io"
$certName = "nocodebi.io"
$tempDir = "$env:TEMP\nocodebi_cert_setup"
$certPath = "$tempDir\$certName.crt"
$keyPath = "$tempDir\$certName.key"
$csrPath = "$tempDir\$certName.csr"
$sanPath = "$tempDir\san.cnf"

# Create working directory
if (-Not (Test-Path $tempDir)) {
    New-Item -ItemType Directory -Path $tempDir | Out-Null
}

Write-Host "`n🔐 Generating Wildcard Certificate for $domain..." -ForegroundColor Cyan

# Generate private key
& openssl genrsa -out $keyPath 2048

# Generate CSR
& openssl req -new -key $keyPath -out $csrPath -subj "/CN=$commonName"

# Create SAN config
"subjectAltName=DNS:$domain,DNS:$certName" | Out-File -Encoding ascii $sanPath

# Generate self-signed cert with SAN
& openssl x509 -req -in $csrPath -signkey $keyPath -out $certPath -days 825 -extfile $sanPath

Write-Host "`n✅ Certificate generated at: $certPath" -ForegroundColor Green

# Add to Root Certificate Store
Write-Host "`n🔐 Installing certificate to ROOT store..." -ForegroundColor Cyan

$cert = New-Object System.Security.Cryptography.X509Certificates.X509Certificate2
$cert.Import($certPath)

$store = New-Object System.Security.Cryptography.X509Certificates.X509Store("Root", "LocalMachine")
$store.Open("ReadWrite")
$store.Add($cert)
$store.Close()

Write-Host "`n✅ Certificate installed successfully." -ForegroundColor Green

# Update hosts file
$hostsFile = "$env:WINDIR\System32\drivers\etc\hosts"
$entry = "127.0.0.1 local-product.nocodebi.io"
$hostsContent = Get-Content $hostsFile

if ($hostsContent -notcontains $entry) {
    Add-Content -Path $hostsFile -Value $entry
    Write-Host "`n🌐 Added entry to hosts file: $entry" -ForegroundColor Green
} else {
    Write-Host "`nℹ️ Hosts entry already exists: $entry" -ForegroundColor Yellow
}

# Done
Write-Host "`n🎉 Setup complete. You can now access https://local-product.nocodebi.io" -ForegroundColor Green
Write-Host "   If using a web server, ensure it listens on 127.0.0.1 with SSL using this cert." -ForegroundColor Gray

# Optional cleanup
# Remove-Item -Recurse -Force $tempDir
