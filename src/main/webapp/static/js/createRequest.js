let createRequest = function() {

    let amount;
    let type;
    let desc;
    
    let getAmount = function() {

    }

    let getType = function() {

    }

    let getDesc = function() {

    }

    let createBody = function(amount, type, desc) {
        let body = {
            "amount": amount,
            "type": type,
            "desc": desc
        };

        return body;
    }

    amount = getAmount();
    type = getType();
    desc = getDesc();

    let json = createBody();
}