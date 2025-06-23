# build jar
mvn clean install -Dmaven.test.skip=true
image_version="v1"
project_list=("fraud")

# move jar to 172.16.31.24
for project in "${project_list[@]}"
  do
    echo "moving $project";
    jar_file="fraud-starter/target/fraud.jar"
    scp "${jar_file}" /root/projects/fraud/fraud/dockers/
  done

# build docker image

for project in "${project_list[@]}"
  do
    echo "building $project";
    docker build -t "fraud:${image_version}" /root/projects/fraud/fraud/dockers/
    docker image tag "fraud:${image_version}" "harbor.streamcomputing.com/lark/fraud:${image_version}"
    docker login -u xinghao.wang@streamcomputing.com -p Welcome! harbor.streamcomputing.com
    docker push "harbor.streamcomputing.com/lark/fraud:${image_version}"
  done


# deploy project
kubectl delete pods -n ma0uqkj4-fraud $(kubectl get pods -n ma0uqkj4-fraud | grep fraud | awk '{print $1}')
