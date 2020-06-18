## Introduction
This is a fork of the implementation of Open-QKD-Network's [qkd-net](https://github.com/Open-QKD-Network/qkd-net) project. The reason for forking is to implement additional functionality for demo purposes that goes beyond what the creators have included. It also allows me to include this user guide, as I found the original documentation quite sparse which resulted in me having to experiment a lot. This user guide should guide the user through the entire process of installation and running a demo of `qkd-net` as quickly as possible, with minimal tinkering. It will also give the user a brief but sufficient understanding of the relevant components of the code, such that the user can easily extend its functionalities where needed.

## Requirements
The user should be running a Linux OS. Users on Windows can obtain a Linux environment by [Windows Subsystem for Linux](https://docs.microsoft.com/en-us/windows/wsl/install-win10), while those on Windows and Mac can use a virtual machine such as [VirtualBox](https://www.virtualbox.org/). The latter is recommended as it allows the user to spin up multiple nodes on the same PC. The other option is to of course install Linux natively as a bootable OS.

**As of the time of writing, `qkd-net` is NOT compatible with OpenSSL versions 1.1.0 and higher.** Check your OpenSSL version by:
```
$ openssl version
OpenSSL 1.0.2g  1 Mar 2016
```
This is a problem as most recent Linux OS ship with > OpenSSL 1.1.0, and it is not easy to downgrade OpenSSL. Hence, the user is adviced to install [Ubuntu 16.04 LTS](https://releases.ubuntu.com/16.04/), which ships with version 1.0.2g.

On a fresh install of Ubuntu, the user should
```
$ sudo apt update
$ sudo apt upgrade
```
before proceeding to install the dependencies `qkd-net` rely on:
```
$ sudo apt-get install git
$ sudo apt-get install openjdk-8-jdk
$ sudo apt-get install maven
$ sudo apt-get install screen
$ sudo apt-get install libjson-c-dev
$ sudo apt-get install openssl libssl-dev
$ sudo apt-get install libcurl4-openssl-dev
```
OpenJDK 8 is used as it is the version this was tested on, although newer versions should run fine.

## Installation
Here we show how to get a 4-node QKD network running. We call the 4 nodes A, B, C and D, with IP addresses `192.168.121`, `192.168.1.76`, `192.168.1.47` and `192.168.1.208` respectively. (instructions on how to find IP add of your system is shown later). For simplicity, the topology of the network is A--B--C--D, i.e the nodes are connected in a linear chain.

1. Assuming we are on system A, clone the repository using
```
$ git clone https://github.com/ajrheng/qkd-net.git
```
2. then `cd` into the directory
```
$ cd qkd-net/kms
```
3. and build the source files
```
$ ./scripts/build
```
This command can take several minutes and spit out massive amounts of text on the command line, especially on first run. What is happening is the system is using the `maven` library to download required Java dependencies and compiling all the source (`.java` files) to executable programs that we can run later.

4. Now we want to generate the config files that the program depends on, so run in order:
```
$ cd ../qkd-config
$ tar xvf a.tar
$ rm -r ~/.qkd
$ mv .qkd ~/
```
What happens is the building (step 3) generates a folder at `~/.qkd` that we somehow do not want, so we remove it with `rm`. We generate the `.qkd` folder that we want from untar-ing the provided `a.tar` and moving it to `~/`. (`~` refers to the `$HOME` folder in Unix systems. In my case it is `/home/alvin`).
5. The network topology is encoded in the file `routes.json` located at `~/.qkd/qnl/routes.json`. It looks like this
```
{ 
  "adjacent": {
    "B": "192.168.1.76"
  },
  "nonAdjacent": {
  }  
}
```
IP addresses of adjacent nodes are stored under "adjacent". Since only node B is adjacent to A, this has been pre-set in the provided folder. **Update the IP addresses for your nodes accordingly, as they will differ from mine**.

6. Now to run the executables
```
$ cd ../kms
$ ./scripts/run
```
This process takes about 1-2 minutes. To see that this has been executed successfully, perform
```
$ screen -ls
There are screens on:
	3753.kms-routing-svc	(18/06/2020 11:50:00)	(Detached)
	3750.kms-qnl-svc	(18/06/2020 11:50:00)	(Detached)
	3690.kms-gw	(18/06/2020 11:49:40)	(Detached)
	3634.kms-svc	(18/06/2020 11:49:20)	(Detached)
	3578.auth-svc	(18/06/2020 11:49:00)	(Detached)
	3499.reg-svc	(18/06/2020 11:48:40)	(Detached)
	3453.config-svc	(18/06/2020 11:48:30)	(Detached)
7 Sockets in /var/run/screen/S-alvin.
```
The `run.sh` script that was executed ran the Java executables that we compiled earlier in 'detached' `screen` windows. You can see these are the relevant services in the QKD network, such as the KMS-QNL service, KMS-routing service and so on. In essence, `qkd-net` requires all these services to be run simultaneously, so the developers run each of these services in a separate terminal in the background ('detached'), which is what `screen` does. 
