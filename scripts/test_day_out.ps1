# Test Case 1: Day Out Request (Same-day, nights = 0, Auto-Approved)
$headers = @{ "Content-Type" = "application/json" }
$body = @{
    studentName = "Sudhanshu Kumar"
    regNo       = "RA2311003010001"
    hostelBlock = "M-Block, Room 412"
    outDate     = (Get-Date).AddDays(2).ToString("yyyy-MM-dd")
    returnDate  = (Get-Date).AddDays(2).ToString("yyyy-MM-dd")
    parentPhone = "+919876543210"
    purpose     = "Bookstore and local market visit"
} | ConvertTo-Json

Write-Host "Sending Day-Out Request (Auto-Approval)..." -ForegroundColor Cyan
$response = Invoke-RestMethod -Uri "http://localhost:8080/outpass" -Method Post -Headers $headers -Body $body
Write-Host "Response received:" -ForegroundColor Green
$response | ConvertTo-Json
