pragma solidity ^0.4.24;

// Interface for Asset Registries
interface AssetRegisterInterface {
    function issueAssets(address _recipient, uint _amount) external;
    function transfer(address _sender, address _recipient, uint _amount) external;
    function getBalance(address _user) external view returns (uint);
}

contract FungibleAsset is AssetRegisterInterface {
    address registryOwner;

    mapping (address => uint) balances;

    event AssetRegisterEvent(address sender, address recipient, uint amount, uint balance_sender, uint balance_recipient);
  
    constructor(address _owner) public {
        registryOwner = _owner;
    }

    function revokeAllAssets(address _recipient) public {
        // if (msg.sender != registryOwner) {
        //     revert("Assets can only be revoked by the registry owner");
        // }

        // uint _amount = balances[_recipient];

        balances[_recipient] = 0;

        emit AssetRegisterEvent(_recipient, 0x00, 0, 0, 0);
    }

    function issueAssets(address _recipient, uint _amount) public {
        // if (msg.sender != registryOwner) {
        //     revert("Assets can only be issued by the registry owner");
        // }

        balances[_recipient] += _amount;

        emit AssetRegisterEvent(0x00, _recipient, _amount, 0, balances[_recipient]);
    }

    function transfer(address _sender, address _recipient, uint _amount) public {
        // if (msg.sender != registryOwner) {
        //     revert("Assets can only be transferred by the registry owner");
        // }

        if (balances[_sender] < _amount) {
            revert("Not enough balance");
        }

        balances[_sender] -= _amount;
        balances[_recipient] += _amount;

        emit AssetRegisterEvent(_sender, _recipient, _amount,balances[_sender],balances[_recipient]);
    }

    function getBalance(address _user) public view returns (uint){
      return balances[_user];
    }
}
