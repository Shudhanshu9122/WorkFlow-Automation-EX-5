# Camunda 8 SaaS Setup & Execution Guide

Follow these steps to connect your local Spring Boot application to Camunda 8 Cloud.

---

## Part 1: Camunda 8 Cloud Console Configuration

### 1. Log in to Camunda Console
Navigate to [https://console.camunda.io](https://console.camunda.io) and log in with your account.

### 2. Create a Cluster
1. In the left navigation bar, click **Clusters**.
2. Click **Create new cluster**.
3. Configure the cluster:
   * **Name:** `outpass-demo` (or `leave-demo`)
   * **Region:** Select your nearest geographic region (e.g., `bru-2`, `us-east-1`).
   * **Generation:** Select `8.8` generation.
4. Click **Create**. Wait 2-3 minutes until the status shows **Healthy** (green).

### 3. Generate API Client Credentials
1. Open your healthy cluster and navigate to the **API** tab.
2. Click **Create new client**.
3. Name the client `outpass-app`.
4. Ensure the **Zeebe / Orchestration Cluster** scope is checked.
5. Click **Create**.
6. In the modal that appears, switch to the **Spring Boot** tab. Copy the 4 configuration values:
   * `Client ID`
   * `Client Secret`
   * `Cluster ID`
   * `Region`

> [!WARNING]
> The Client Secret is only shown once! Save it securely or set it as environment variables.

---

## Part 2: Configuring Your Local Project

You can supply the credentials in `src/main/resources/application.yaml` or through environment variables (recommended for security):

### Option A: Via Environment Variables (Recommended)
```bash
# PowerShell
$env:CAMUNDA_CLIENT_ID="your_client_id"
$env:CAMUNDA_CLIENT_SECRET="your_client_secret"
$env:CAMUNDA_CLUSTER_ID="your_cluster_id"
$env:CAMUNDA_CLUSTER_REGION="bru-2"

# Linux / macOS Bash
export CAMUNDA_CLIENT_ID="your_client_id"
export CAMUNDA_CLIENT_SECRET="your_client_secret"
export CAMUNDA_CLUSTER_ID="your_cluster_id"
export CAMUNDA_CLUSTER_REGION="bru-2"
```

### Option B: Directly in `src/main/resources/application.yaml`
```yaml
camunda:
  client:
    mode: saas
    auth:
      client-id: <your-client-id>
      client-secret: <your-client-secret>
    cloud:
      cluster-id: <your-cluster-id>
      region: <your-region>
```

---

## Part 3: Running the Application

In your project root directory, run:
```bash
# Using Maven Wrapper (Windows)
.\mvnw.cmd spring-boot:run

# Using Maven Wrapper (Linux / macOS)
./mvnw spring-boot:run
```

Look for the startup log lines confirming:
1. Process definitions `hostel-outpass` and `student-leave` deployed.
2. Forms `warden-approval-form` and `fa-approval-form` deployed.
3. Job workers `check-outpass`, `issue-outpass`, `validate-leave`, `notify-student` registered and polling.

---

## Part 4: Testing & Monitoring in Operate and Tasklist

### 1. Verification in Operate
1. Go to your cluster in [Camunda Console](https://console.camunda.io).
2. Click **Operate**.
3. Select **Processes** from the top bar:
   * Verify `Hostel Out-Pass Workflow` and `Student Leave Application` appear in the process catalog.

### 2. Human Task Completion in Tasklist
When an overnight out-pass (nights &ge; 1) or long leave (&gt; 2 days) is requested:
1. Click **Tasklist** from the Camunda Console.
2. Locate the pending task (e.g., `Warden approval`).
3. Click **Assign to me**.
4. Review student data displayed on the form.
5. Tick **Approve Out-Pass Request** and type remarks.
6. Click **Complete Task**.
7. Return to **Operate** to observe the process token advance through the merge gateway to `Issue out-pass` and conclude at the end event!
