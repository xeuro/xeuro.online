#!/usr/bin/env bash

# see:
## https://github.com/web3j/web3j#java-smart-contract-wrappers
## https://docs.web3j.io/smart_contracts.html#smart-contract-wrappers

# compile:
solc --version
solc ./src/main/resources/smart-contracts/xEuro.sol --bin --abi --optimize --gas --overwrite -o ./src/main/resources/smart-contracts/ > ./src/main/resources/smart-contracts/xEuro.sol.gas.estimation.txt

#./web3j-4.1.1/bin/web3j solidity generate -b ./web3jsNode/contracts/xEuro.bin -a ./web3jsNode/contracts/xEuro.abi -o ./src/main/java -p com.appspot.euro2ether.contracts
./web3j-4.2.0/bin/web3j solidity generate -b ./src/main/resources/smart-contracts/xEuro.bin -a ./src/main/resources/smart-contracts/xEuro.abi -o ./src/main/java -p com.appspot.euro2ether.contracts

cp -f ./src/main/resources/smart-contracts/xEuro.abi ./src/main/webapp/app/smart-contract/
