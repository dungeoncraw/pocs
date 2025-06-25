
# 📦 Helm Chart - Hello World App (NGINX on Kubernetes)

This project is a simple example of a **Helm Chart** to deploy an **NGINX container** inside a **local Kubernetes cluster** (e.g., Minikube).

## ✅ Prerequisites

Before starting, make sure you have the following tools installed on your Mac:

| Tool | Installation |
|---|---|
| **Homebrew** | [https://brew.sh/](https://brew.sh/) |
| **Helm** | `brew install helm` |
| **kubectl** | `brew install kubectl` |
| **Minikube (or Kind)** | `brew install minikube` or `brew install kind` |

## 🚀 Step-by-Step: Installation and Execution

### 1. Start a Local Kubernetes Cluster

#### Using Minikube:

```bash
minikube start
```
Enable ingress
```bash
minikube addons enable ingress
```
### 2. Clone Locally

Clone the repo

```
hello-world-app/
├── Chart.yaml
├── values.yaml
└── templates/
    ├── deployment.yaml
    ├── service.yaml
    ├── configmap.yaml
    ├── secret.yaml
    └── ingress.yaml
```

### 3. Install the Helm Chart

```bash
helm install bread-release ./sample
```

### 4. Check the Created Resources

```bash
kubectl get pods
kubectl get svc
```

### 5. Accessing NGINX Locally

Using Minikube:

```bash
minikube service bread-release-service
```

Add the host to etc config
```bash
echo "$(minikube ip) sample.local" | sudo tee -a /etc/hosts
```

Then open: http://sample.local

## 🧹 How to Remove the Deployment

```bash
helm uninstall hello-release
minikube stop
```

## Adding grafana

```bash
helm repo add grafana https://grafana.github.io/helm-charts
helm repo update
```

```bash
helm install grafana grafana/grafana -f grafana-values.yaml
```

Access the minikub ip
```bash
minikube ip
```

Accessing grafana
```bash
http://<minikube-ip>:30001
```
## ✅ Next Steps

- Add Ingress Controller
- Configure ConfigMaps and Secrets
- Create CI/CD Pipelines with Helm
