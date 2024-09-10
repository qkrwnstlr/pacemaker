pipeline {
    agent any

    stages {
        stage('Build Backend') {
            steps {
                script {
                    // Backend 디렉토리로 이동
                    dir('backend') {
                        // Docker 이미지 빌드
                        sh 'docker build -t pacemaker-backend-image .'
                        
                        // 기존 컨테이너가 존재하면 제거
                        sh '''
                        if [ "$(docker ps -aq -f name=pacemaker-backend-container)" ]; then
                            docker rm -f pacemaker-backend-container
                        fi
                        '''
                        
                        // Docker 컨테이너 실행
                        sh '''
                        docker run -d \
                            --name pacemaker-backend-container \
                            -e TZ=Asia/Seoul \
                            --network pacemaker-network \
                            pacemaker-backend-image
                        '''
                    }
                }
            }
        }
    }

    post {
        always {
            echo 'Pipeline finished.'
        }
        failure {
            echo 'Pipeline failed.'
        }
    }
}