sudo docker images
sudo docker build -t xiaoyuzhang321/scone_mr:SGXhadoop2.7new .
sudo docker push xiaoyuzhang321/scone_mr:SGXhadoop2.7new 
sudo docker rmi -f IMAGE_ID
#############################################################################
sudo docker run --privileged  --device=/dev/isgx  -it xiaoyuzhang321/scone_mr:SGXhadoop2.7new /etc/bootstrap.sh -bash
hese containers can include special libraries needed by the application, and they can have different versions of Perl, Python, and even Java than what is installed on the NodeManager
cat > HelloWorld.java << EOF
public class HelloWorld {

    public static void main(String[] args) {
        System.out.println("Hello World");
    }

}
EOF

javac HelloWorld.java

SCONE_VERSION=1 SCONE_LOG=7 SCONE_HEAP=8G java HelloWorld

/usr/sbin/sshd  && \
ssh-keygen -t dsa -P ""  && \
ssh-keygen -t rsa -P ""  && \
ssh-keygen -t ecdsa -P ""  && \
ssh-keygen -t ed25519 -P ""  && \
cat ~/.ssh/id_rsa.pub >> ~/.ssh/authorized_keys && \
cat ~/.ssh/id_dsa.pub >> ~/.ssh/authorized_keys && \
chmod 600 ~/.ssh/authorized_keys && \
chmod 700 ~/.ssh/

#cd /usr/local/hadoop/sbin
hadoop namenode -format && \
start-all.sh && \
hadoop fs -mkdir /input && \
hadoop fs -mkdir /output && \
hadoop fs -put ../README.txt /input/Test.txt && \
SCONE_LOG=7 SCONE_HEAP=12G time ../bin/hadoop jar ../share/hadoop/mapreduce/hadoop-mapreduce-examples-2.7.7.jar wordcount /input/Test.txt /output/Test
../bin/hadoop fs -rmr /output/Test
#########################################################################################
sudo groupadd docker     #添加docker用户组
sudo gpasswd -a $USER docker     #将登陆用户加入到docker用户组中
newgrp docker     #更新用户组
sudo systemctl restart docker
sudo chown "$USER":"$USER" /home/"$USER"/.docker -R

sudo chmod g+rwx "/home/$USER/.docker" -R
 docker ps --no-trunc    #测试docker命令是否可以使用sudo正常使用

hadoop jar /home/xidian/hadoop-2.7.7/share/hadoop/mapreduce/hadoop-mapreduce-examples-2.7.7.jar wordcount -Dmapreduce.map.env="yarn.nodemanager.docker-container-executor.image-name=xiaoyuzhang321/scone_mr:SGXhadoop2.7new" -Dmapreduce.reduce.env="yarn.nodemanager.docker-container-executor.image-name=xiaoyuzhang321/scone_mr:SGXhadoop2.7new" -Dyarn.app.mapreduce.am.env="yarn.nodemanager.docker-container-executor.image-name=xiaoyuzhang321/scone_mr:SGXhadoop2.7new" /input/Test.txt /output/Test

hadoop jar /home/xidian/class/wc.jar  MRR_Solution.WordCount_MRR1 -Dmapreduce.map.env="yarn.nodemanager.docker-container-executor.image-name=xiaoyuzhang321/scone_mr:SGXhadoop2.7new" -Dmapreduce.reduce.env="yarn.nodemanager.docker-container-executor.image-name=xiaoyuzhang321/scone_mr:SGXhadoop2.7new" -Dyarn.app.mapreduce.am.env="yarn.nodemanager.docker-container-executor.image-name=xiaoyuzhang321/scone_mr:SGXhadoop2.7new" /input/trip_jan.txt /output/Trip11 /output/Trip22

hadoop jar /home/xidian/class/fullshuffle1j.jar  solution_in_paper.Full_Shuffle_1j -Dmapreduce.map.env="yarn.nodemanager.docker-container-executor.image-name=xiaoyuzhang321/scone_mr:SGXhadoop2.7new" -Dmapreduce.reduce.env="yarn.nodemanager.docker-container-executor.image-name=xiaoyuzhang321/scone_mr:SGXhadoop2.7new" -Dyarn.app.mapreduce.am.env="yarn.nodemanager.docker-container-executor.image-name=xiaoyuzhang321/scone_mr:SGXhadoop2.7new" /input/Ecensus.txt /output/census  282s/191s

hadoop jar /home/xidian/class/safeshuffle.jar  solution_in_paper.Full_Shuffle_2j -Dmapreduce.map.env="yarn.nodemanager.docker-container-executor.image-name=xiaoyuzhang321/scone_mr:SGXhadoop2.7new" -Dmapreduce.reduce.env="yarn.nodemanager.docker-container-executor.image-name=xiaoyuzhang321/scone_mr:SGXhadoop2.7new" -Dyarn.app.mapreduce.am.env="yarn.nodemanager.docker-container-executor.image-name=xiaoyuzhang321/scone_mr:SGXhadoop2.7new" /input/Ecensus.txt /output/im1 /output/im2  5m24s
#############################################################################
DockerContainerExecutor.java
cd /hadoop-2.7.7-src/hadoop-yarn-project/hadoop-yarn/hadoop-yarn-server/hadoop-yarn-server-nodemanager
mvn package -Pdist,native,docs -DskipTests -Dtar  #编译nodemanger
***********************/class
hadoop com.sun.tools.javac.Main ./MRR_Solution/WordCount_MRR1.java
jar cf wc.jar ./MRR_Solution/WordCount*.class

hadoop com.sun.tools.javac.Main ./shuffle_in_the_middle_solution/Middle_Shuffle.java ./shuffle_in_the_middle_solution/JAES.java
jar cf middleshuffle.jar ./shuffle_in_the_middle_solution/Middle_Shuffle*.class ./shuffle_in_the_middle_solution/JAES.class 


