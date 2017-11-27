
view = {
    loginView: document.getElementById('login'),
    expenseView: document.getElementById('expense-view'),
    directoryView: document.getElementById('directory'),

    swapView: function(curView, nextView) {

        curView.addEventListener("transitionend", (e) => {
            curView.classList.add("hidden");
            nextView.classList.remove("hidden");
            setTimeout(() => {
                nextView.classList.remove("invis");
            }, 100);
            
        }, {once: true})
        //view.removeModal(curView);
        curView.classList.add("invis");
    },

    hideLoading: () => {
        //Get elements
        modalBackdrop = document.getElementById("modal-backdrop");
        loadingModal = document.getElementById("ers-loading-modal");

        //Set listener to display none after opacity transition ends
        modalBackdrop.addEventListener("transitionend", function(e) {
            modalBackdrop.classList.add('hidden');
            this.removeEventListener(e.type, arguments.callee);
        }, {once: true});

        loadingModal.addEventListener('transitionend', function(e) {
            loadingModal.classList.add('hidden');
            this.removeEventListener(e.type, arguments.callee);
        }, {once: true});

        //Begin opacity transition
        modalBackdrop.classList.add('invis')
        loadingModal.classList.add('invis');
    },

    showLoading: () => {
        //Get elements
        modalBackdrop = document.getElementById("modal-backdrop");
        loadingModal = document.getElementById("ers-loading-modal");

        //Begin opacity transition
        modalBackdrop.classList.remove('hidden');
        loadingModal.classList.remove('hidden');

        //Display: none can't be transitioned, and removing hidden and invis at the same time will skip transition
        //So, delay the removal of invis to ensure transition runs
        setTimeout((e) => {
            loadingModal .classList.remove('invis');
            modalBackdrop.classList.remove('invis');
        }, 50);
    },
    
    showUserbar: () => {
        userbar.show();
    },

    directory: {
        history: document.getElementById('request-history'),
        create:  document.getElementById('create-request'),
        audit:   document.getElementById('audit-request'),
        
        update: function() {
            console.log(this.audit);
            if(user.getRole() === 'FINANCE_MANAGER') {
                this.audit.classList.remove('hidden');
            } else {
                this.audit.classList.add('hidden');
            }
        }
    },

    reimbursements: {
        update: () => {
            oldBody = document.getElementById('reimbursement-table-body');
            newBody = document.createElement('tbody');
            newBody.id = 'reimbursement-table-body';
            
            for(i in user.reimbursements) {
                let reimb = user.reimbursements[i];
                console.log("Iteration");
                console.log(reimb);
                let rowE = newBody.insertRow(-1);
                
                let amountE = rowE.insertCell(-1);
                let submitE = rowE.insertCell(-1);
                let resolveE = rowE.insertCell(-1);
                let statusE = rowE.insertCell(-1);
                let descE = rowE.insertCell(-1);
                //let typeE = rowE.insertCell(-1);

                amountE.classList.add('amount');
                submitE.classList.add('date');
                resolveE.classList.add('date');
                statusE.classList.add('status');
                descE.classList.add('description');
                //typeE.classList.add('reimb-type');

                amountE.innerText = '$' + reimb.amount.toFixed(2);
                
                submitE.innerText = reimb.author.firstName + " " + reimb.author.lastName;

                if(reimb.resolver === null) {
                    resolveE.innerText = "--";
                } else {
                    resolveE.innerText = reimb.resolver.firstName + " " + reimb.author.lastName;
                }
                //typeE.innerText = reimb.type;
                statusE.innerText = reimb.status;
                descE.innerText = reimb.description;
 
            }

            oldBody.parentNode.replaceChild(newBody, oldBody);

        }
    }
    
}