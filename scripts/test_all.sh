#!/usr/bin/env bash
# Quick curl test suite for Camunda 8 Outpass & Leave Workflow

BASE_URL="http://localhost:8080"

echo "=== Test 1: Day Out Request (Auto-Approved) ==="
curl -s -X POST "${BASE_URL}/outpass" \
  -H "Content-Type: application/json" \
  -d '{
    "studentName": "Sudhanshu Kumar",
    "regNo": "RA2311003010001",
    "hostelBlock": "M-Block, Room 412",
    "outDate": "2026-10-25",
    "returnDate": "2026-10-25",
    "parentPhone": "+919876543210",
    "purpose": "Local market visit"
  }' | jq . || cat
echo -e "\n"

echo "=== Test 2: Overnight Outpass (Needs Warden Approval) ==="
curl -s -X POST "${BASE_URL}/outpass" \
  -H "Content-Type: application/json" \
  -d '{
    "studentName": "Aakash Sharma",
    "regNo": "RA2311003010045",
    "hostelBlock": "O-Block, Room 204",
    "outDate": "2026-10-25",
    "returnDate": "2026-10-28",
    "parentPhone": "+919812345678",
    "purpose": "Hackathon"
  }' | jq . || cat
echo -e "\n"

echo "=== Test 3: Invalid Outpass (Return before Out -> Rejected) ==="
curl -s -X POST "${BASE_URL}/outpass" \
  -H "Content-Type: application/json" \
  -d '{
    "studentName": "Rohan Verma",
    "regNo": "RA2311003010099",
    "hostelBlock": "N-Block, Room 102",
    "outDate": "2026-10-28",
    "returnDate": "2026-10-25",
    "parentPhone": "+919899887766",
    "purpose": "Vacation"
  }' | jq . || cat
echo -e "\n"

echo "=== Test 4: Short Student Leave (Auto-Approved) ==="
curl -s -X POST "${BASE_URL}/leave" \
  -H "Content-Type: application/json" \
  -d '{
    "studentName": "Priya",
    "regNo": "RA2311003010001",
    "fromDate": "2026-10-25",
    "toDate": "2026-10-26",
    "reason": "Medical appointment"
  }' | jq . || cat
echo -e "\n"
