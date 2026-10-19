# Test Case 3: Invalid Outpass Request (Return date before out date -> REJECTED)
$headers = @{ "Content-Type" = "application/json" }
$body = @{
    studentName = "Rohan Verma"
    regNo       = "RA2311003010099"
    hostelBlock = "N-Block, Room 102"
    outDate     = (Get-Date).AddDays(5).ToString("yyyy-MM-dd")
    returnDate  = (Get-Date).AddDays(2).ToString("yyyy-MM-dd") # Invalid: before outDate
    parentPhone = "+919899887766"
    purpose     = "Vacation"
} | ConvertTo-Json

Write-Host "Sending Invalid Dates Request (Should be Rejected by Gateway)..." -ForegroundColor Red
$response = Invoke-RestMethod -Uri "http://localhost:8080/outpass" -Method Post -Headers $headers -Body $body
Write-Host "Response received:" -ForegroundColor Green
$response | ConvertTo-Json
Write-Host "Check Operate: Process moves directly to issue-outpass worker with passStatus = REJECTED" -ForegroundColor Cyan
