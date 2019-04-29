#!/usr/bin/env bash

# Slither (https://github.com/crytic/slither)
slither src/main/resources/smart-contracts/xEuro.sol

#Manticore (https://github.com/trailofbits/manticore)
manticore src/main/resources/smart-contracts/xEuro.sol --contract xEuro

