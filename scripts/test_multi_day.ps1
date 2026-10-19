# Test Case 2: Multi-day Outpass Request (nights >= 1, Requires Warden Approval)
$headers = @{ "Content-Type" = "application/json" }
$body = @{
    studentName = "Aakash Sharma"
    regNo       = "RA2311003010045"
    hostelBlock = "O-Block, Room 204"
    outDate     = (Get-Date).AddDays(2).ToString("yyyy-MM-dd")
    returnDate  = (Get-Date).AddDays(5).ToString("yyyy-MM-dd")
    parentPhone = "+919812345678"
    purpose     = "Hackathon competition at IIT Madras"
} | ConvertTo-Json

Write-Host "Sending Multi-day Request (Requires Warden Approval in Tasklist)..." -ForegroundColor Yellow
$response = Invoke-RestMethod -Uri "http://localhost:8080/outpass" -Method Post -Headers $headers -Body $body
Write-Host "Response received:" -ForegroundColor Green
$response | ConvertTo-Json
Write-Host "Action required: Open Camunda Tasklist -> Assign to me -> Tick Approve -> Complete Task." -ForegroundColor Magenta
