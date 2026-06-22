pipeline {
    agent any

    environment {
        DOCKER_BUILDKIT = "0"
        COMPOSE_DOCKER_CLI_BUILD = "0"
    }

    stages {

        // 🔹 1. Clone
        stage('Clone Repository') {
            steps {
                git branch: 'main', url: 'https://github.com/Rahul-Kali/application-project.git'
            }
        }

        // 🔹 2. Build Backend
        stage('Build Spring Boot Services') {
            steps {
                sh '''
                cd user-service && mvn clean package -DskipTests
                cd ../product-service && mvn clean package -DskipTests
                cd ../order-service && mvn clean package -DskipTests
                '''
            }
        }

        // 🔹 3. Docker Compose Test
        stage('Docker Compose Test') {
            steps {
                sh '''
                docker-compose down || true
                docker-compose up -d --build

                echo "Waiting for frontend API route..."
                for i in $(seq 1 30); do
                  if curl -fsS -X POST http://localhost:3000/api/order; then
                    echo "Frontend and microservices are connected."
                    break
                  fi

                  if [ "$i" -eq 30 ]; then
                    echo "Frontend API smoke test failed."
                    docker-compose ps
                    docker-compose logs --no-color frontend order-service user-service product-service payment-service notification-service analytics-service
                    exit 1
                  fi

                  sleep 5
                done

                docker-compose ps
                docker-compose down
                '''
            }
        }

        // 🔥 4. Start Minikube
        stage('Start Minikube') {
    steps {
        sh '''
        echo "🔧 Setting up Minikube..."

        # Clean broken cluster if exists
        minikube delete || true

        echo "🚀 Starting Minikube..."

        minikube start \
          --driver=docker \
          --memory=3072 \
          --cpus=2 \
          --kubernetes-version=v1.28.3 \
          --force

        echo "⏳ Waiting for Kubernetes API..."

        # Wait until node is ready
        kubectl wait --for=condition=Ready node/minikube --timeout=120s

        echo "📌 Setting context..."
        kubectl config use-context minikube

        echo "📊 Cluster status:"
        kubectl get nodes
        kubectl get pods -A

        echo "Enabling Minikube ingress addon..."
        minikube addons enable ingress
        kubectl wait --namespace ingress-nginx \
          --for=condition=ready pod \
          --selector=app.kubernetes.io/component=controller \
          --timeout=120s
        '''
    }
}

        // 🔥 5. Build Images INSIDE Minikube
        stage('Build Images in Minikube') {
            steps {
                sh '''
                echo "Switching to Minikube Docker..."

                eval $(minikube docker-env)

                docker build -t user-service ./user-service
                docker build -t product-service ./product-service
                docker build -t order-service ./order-service

                docker build -t payment-service ./payment-service
                docker build -t notification-service ./notification-service
                docker build -t analytics-service ./analytics-service

                docker build -t frontend ./frontend

                docker images
                '''
            }
        }

        // 🔹 6. Deploy to Kubernetes
        stage('Deploy to Kubernetes') {
            steps {
                sh '''
                echo "Deploying to Kubernetes..."

                kubectl apply -f k8s/

                sleep 15
                '''
            }
        }

        // 🔹 7. Verify
        stage('Verify Deployment') {
            steps {
                sh '''
                kubectl get pods
                kubectl get svc
                kubectl get ingress
                '''
            }
        }
    }

    post {
        success {
            echo '✅ CI/CD Pipeline Completed Successfully!'
        }
        failure {
            echo '❌ Pipeline Failed. Check logs.'
        }
    }
}
