let expenseView = {
    setup: function() {
        setUsername();

    },
    setUsername: function() {
        element = document.getElementById("userbar-username");
        element.innerText = `${user.firstName} ${user.lastName}`;
    }
}

let getNewExpenseBody = function() {
    let amountE = document.getElementById('amount');
    let typeE = document.getElementById('type');
    let descE = document.getElementById('create-description');
    let receiptE = document.getElementById('receipt-img'); 
    let submitE = document.getElementById('expense-create-submit')

    //Disable to prevent further input
    submitE.setAttribute('disabled', true);

    //get values
    let amount = amountE.value;
    let type = typeE.value;
    let desc = descE.value;
    // let receiptLocation = receiptE.value;
    // let file = receiptE.files[0];
    // let reader = new FileReader();
    let jsonObject = {
        "amount": amount,
        "type": type,
        "description": desc
    };

    return jsonObject;
}

let getImageBinary = function() {
    return new Promise(function(resolve, reject) {
        let receiptE = document.getElementById('receipt-img'); 
        let file = receiptE.files[0];
        let reader = new FileReader();
        reader.onload = () => {
            resolve(reader.result);
        }
        reader.onerror = () => {
            reject(Error("Error reading file."));
        }
    
        //reader.readAsArrayBuffer(file);
        return file;
    });
}

let clearNewExpenseData = function() {
    let amountE = document.getElementById('amount');
    let typeE = document.getElementById('type');
    let descE = document.getElementById('description');

    amountE.value = 0;
    typeE.value = '';
    descE.value = '';
}

let submitReimbursement = function() {
    view.showLoading();
    let jsonObject = getNewExpenseBody();
    console.log('Beginning..');
    getImageBinary().then(function(data) {
        //XXX Pipeline functions, but images are not recovered correctly
        //fix data handling to work uniformly between JavaScript and Java
        let strBlob = String.fromCharCode.apply(null, new Uint8Array(data));
        console.log(" Blob -> " + strBlob);
        jsonObject.receipt = strBlob;
        json = JSON.stringify(jsonObject);
        return post('../reimbursement/create', json);
    }).then(function(data) {
        //success
        //clearNewExpenseData();
        
        //update expenditure request view with new data
        return post('../reimbursement');
    }).then(function(data) {
        console.log('Fetching updated data..');
        view.reimbursements.update();
        view.hideLoading();
    }).catch(function(error) {
        //error
        console.log('ERROR');
        console.log(error);
        //TODO show error ribbon

        let submitE = document.getElementById('expense-create-submit')
        
            //Disable to prevent further input
        submitE.removeAttribute('disabled');
        view.hideLoading();

    });  
}

let showCreate = function() {
    let request = document.getElementById('new-request');
    request.classList.remove('collapsed');
}

let hideCreate = function() {
    let request = document.getElementById('new-request');
    request.classList.add('collapsed');
}