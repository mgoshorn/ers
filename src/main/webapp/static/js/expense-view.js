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
    let descE = document.getElementById('description');
    //receiptE = 
    let submitE = document.getElementById('expense-create-submit')

    //Disable to prevent further input
    submitE.addAttribute('disabled');

    //get values
    let amount = amountE.value;
    let type = typeE.value;
    let desc = descE.value;

    let json = {
        "amount": amount,
        "type": type,
        "desc": desc
    };

    return json;
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
    let json = getNewExpenseBody();

    post('../reimbursement/create', json).then(function(data) {
        //success
        clearNewExpenseData();

        //update expenditure request view with new data
        return postMessage('../reimbursement');
    }).then(function(data) {
        user.reimbursements.setup(data);
        view.hideLoading();
    }).catch(function(error) {
        //error
        //TODO show error ribbon

        view.hideLoading();
    });  



}

submitE.addEventListener('click', function() {
    submitReimbursement();
});


