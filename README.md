# porthos-1
Porthos: Macroprogramming and Interoperability of Blockchain Systems

Porthos is a framework for macroprogramming across multiple blockchain systems.  This repository contains the following parts:
1. A DSL embedded in Haskell for writing Porthos contracts together with tools to generate smart contracts in target languages.  Current version translates to Solidity and Go Chaincode and supports multiple Ethereum and Hyperledger instances.
2. A runtime framework to deploy and enable the interoperability between multiple blockchain systems (this version supports Ethereum and Hyperledger instances)

Start by writing a Porthos contract (see Examples) to generate smart contracts
Load the runtime framework and deploy using the framework.

