docker container kill $(docker container ls -q)
docker container rm $(docker container ls -qa)

docker image pull smduarte/sd19-services:latest
docker run --network=sd-net -e 1 -v /var/run/docker.sock:/var/run/docker.sock smduarte/sd19-services:latest
