# Porthos: Macroprogramming and Interoperability of Blockchain Systems

Porthos is a framework for macroprogramming across multiple blockchain systems.  This repository contains the following parts:
1. A DSL embedded in Haskell for writing Porthos contracts together with tools to generate smart contracts in target languages.  Current version translates to Solidity and Go Chaincode and supports multiple Ethereum and Hyperledger instances.
2. A runtime framework to deploy and enable the interoperability between multiple blockchain systems (this version supports Ethereum and Hyperledger instances)

Start by writing a Porthos contract (see Examples) to generate smart contracts
Load the runtime framework and deploy using the framework.

## Requirements and Versions

* GHCI v8.0.2 (for compiling Porthos contracts into Solidity and Chaincode Go)
* Ganache v6.1.3 (for Ethereum Instances)
* Docker Engine v18 (for running Hyperledger Instance)
* Go v1.11.1 (for building Chaincode)
* Web3j v3.6.0 (for generating Java stubs from Solidity)
* Fabric SDK Java 1.3.0 (for connecting to Hyperledger from Java)
* Java v11.0.1 (for running off-chain framework)
