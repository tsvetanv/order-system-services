# Order Processing System (OPS) – CI/CD Handbook

This handbook describes **step-by-step** how to configure, execute, and verify the
**CI/CD pipeline for the Order Processing System (OPS) application**.

It is intentionally aligned in **style, structure, and rigor** with the
**OPS Infrastructure Handbook** and reflects the **exact CI/CD design and commands**
validated during Iteration 4.

> **Important:**  
> This handbook assumes that infrastructure has already been provisioned
> by strictly following the **OPS Infrastructure Handbook**.
> CI/CD does **not** replace infrastructure provisioning.

---

## Tooling & Execution Environment

| Tool                        | Where it MUST run          |
|-----------------------------|----------------------------|
| GitHub Actions              | **GitHub (cloud-managed)** |
| GitHub UI (variables)       | **Web Browser**            |
| AWS CLI (verification only) | **WSL**                    |
| curl (health check)         | **WSL**                    |

**Important rules**

- Terraform is **NOT executed by CI/CD**
- CI/CD pipelines run **only in GitHub Actions**
- Local machines are used **only for verification**
- CI/CD pipelines must be reproducible without local state

---

## Repository Structure (CI/CD Relevant)

```
order-system/
├── order-system-services/
│   ├── .github/
│   │   └── workflows/
│   │       └── ci-cd.yml
│   └── order-service/
│       └── Dockerfile
└── order-system-infrastructure/
    └── terraform/
        └── github-actions-iam.tf
```

---

## CI/CD Scope (Iteration 4)

Included:

- Build and test the Java application
- Build Docker image for `order-service`
- Push image to Amazon ECR
- Deploy new version to ECS Fargate

Explicitly excluded:

- Terraform execution
- Infrastructure creation or destruction
- Multiple environments
- Rollback automation
- Blue/green or canary deployments

---

## Prerequisites

Before proceeding, ensure:

- Infrastructure is provisioned and running
- ECR repository exists
- ECS Cluster and ECS Service exist
- IAM role `ops-github-actions-deployer` exists
- You have commit access to `order-system-services`

All prerequisites are created via **OPS Infrastructure Handbook**.

---

## 1. Configure GitHub Repository Variables (Web Browser)

CI/CD requires one explicit GitHub repository variable.

Navigate to:

```
GitHub → Repository → Settings → Secrets and variables → Actions → Variables
```

Define the following Repository Variable:

| Name             | Value              |
|------------------|--------------------|
| `AWS_ACCOUNT_ID` | `<AWS account ID>` |

This value is **not a secret**.

---

## 2. Configure GitHub Actions Workflow (Local Editor)

Create the workflow file:

```
order-system-services/.github/workflows/ci-cd.yml
```

This file defines the **entire CI/CD pipeline**.

Key characteristics:

- Trigger: `push` to `main`
- Authentication: AWS OIDC (no static keys)
- Deployment: ECS `force-new-deployment`

No other GitHub configuration is required.

---

## 3. CI/CD Execution (GitHub Actions)

### Trigger the pipeline

Push any commit to the `main` branch:

```
git push origin main
```

### Pipeline stages (automatic)

1. Checkout source code
2. Build & test Java application (Java 21)
3. Build Docker image
4. Tag image with:
    - Git SHA (immutable)
    - `latest` (deployment pointer)
5. Push image to Amazon ECR
6. Trigger ECS service redeployment

All steps execute **inside GitHub Actions runners**.

---

## 4. Deployment Verification (WSL)

After the pipeline completes successfully, verify deployment manually.

Retrieve ALB DNS name:

```
terraform -chdir=terraform output -raw alb_dns_name
```

Run health check:

```
curl http://<alb-dns-name>/actuator/health
```

Expected output:

```
{"status":"UP"}
```

---

## 5. Manual Deployment Fallback (WSL)

If CI/CD is temporarily unavailable, deployment can be triggered manually:

```
aws ecs update-service --cluster ops-ecs-cluster --service ops-order-service --force-new-deployment
```

This command is identical to the CI/CD deployment step.

---

## CI/CD Ownership Model

| Concern                     | Owner              |
|-----------------------------|--------------------|
| Infrastructure provisioning | Terraform (manual) |
| CI pipeline execution       | GitHub Actions     |
| Container image lifecycle   | CI/CD              |
| Runtime orchestration       | ECS Fargate        |
| Health & availability       | ALB + ECS          |

---

## Operational Notes

- CI/CD is an **external delivery mechanism**
- CI/CD does **not appear in runtime or deployment diagrams**
- Infrastructure can always be destroyed independently
- CI/CD can be disabled without affecting infrastructure

---

## CI/CD Scope Summary (Iteration 4)

CI/CD automates **application delivery only**.

Infrastructure remains:

- intentionally manual
- explicitly controlled
- fully reproducible via Terraform

This separation is **architectural by design**, not a limitation.

---

**End of CI/CD Handbook**
