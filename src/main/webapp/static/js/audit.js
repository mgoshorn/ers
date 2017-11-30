audit = {
    pending: undefined,

    fetchPending: function() {
        
        return new Promise(function(resolve, reject) {
            post('../reimbursement/pending').then(function(data) {
                console.log('Data retrieved!');
                audit.pending = JSON.parse(data);
                return this.pending = JSON.parse(data);
            }).then(resolve()).catch(reject(Error()));
        })
        
    },

    update: () => {
        return new Promise(function(resolve, reject) {
            
            oldBody = document.getElementById('expense-table-body');
            newBody = document.createElement('tbody');
            newBody.id = 'expense-table-body';
    
            console.log('Building...');
            console.log(audit.pending);
            for(i in audit.pending) {
                let req = audit.pending[i];
    
                console.log('Building for reimbursement ' + req.id);

                //Create details row
                let detailsRowE = newBody.insertRow(-1);
    
                let amountE = detailsRowE.insertCell(-1);
                let requesterE = detailsRowE.insertCell(-1);
                let requestedE = detailsRowE.insertCell(-1);
                let typeE = detailsRowE.insertCell(-1);
                let receiptE = detailsRowE.insertCell(-1);
                let auditE = detailsRowE.insertCell(-1);
    
                //create description row
                let descriptionRowE = newBody.insertRow(-1);
    
                let titleE = descriptionRowE.insertCell(-1);
                let contentE = descriptionRowE.insertCell(-1);
    
                amountE.classList.add('amount');
                requesterE.classList.add('requester');
                requestedE.classList.add('date');
                typeE.classList.add('type');
                receiptE.classList.add('receipt');
                auditE.classList.add('audit-action');
                contentE.classList.add('description');
    
                //create buttons
                let approveE = document.createElement('button');
                let denyE = document.createElement('button');
    
                approveE.classList.add('btn');
                approveE.classList.add('btn-success');
                approveE.innerText = 'Approve';
                approveE.setAttribute('onclick', `audit.approve(this, ${req.id})`);

                denyE.classList.add('btn');
                denyE.classList.add('btn-warning');
                denyE.innerText = 'Deny';
                denyE.setAttribute('onclick', `audit.deny(this, ${req.id})`);

                //append buttons
                auditE.appendChild(approveE);
                auditE.appendChild(denyE);

                //create anchor
                let receiptLinkE = document.createElement('a');
                receiptLinkE.href = `../reimbursement/receipt/${req.id}`;
                receiptLinkE.innerText = 'receipt';
                receiptE.appendChild(receiptLinkE);
    
                amountE.innerText = '$' + req.amount.toFixed(2);
                requesterE.innerText = req.author.firstName + ' ' + req.author.lastName;
                d = req.submitted;
                let min = d.minute >= 10 ? d.minute : '0' + d.minute;
                let hour = d.hour >= 10 ? d.hour : '0' + d.hour;
                let second = d.second >= 10 ? d.second : '0' + d.second;

                //More abbrievated date
                requestedE.innerText = d.year + '-' + d.monthValue + '-' + d.dayOfMonth + ' ';
                requestedE.innerText += min + ':' + hour + ':' + second;

                // requestedE.innerText = d.dayOfWeek + ', ';
                // requestedE.innerText+= d.month + ' ' + d.dayOfMonth + ' ';
                // requestedE.innerText+= d.hour + ':' + d.minute + ':' + d.second + ' (' + d.year + ')';
                //Monday, November 22 12:00:00 (2017) 
                typeE.innerText = req.type;
                titleE.innerText = 'DESCRIPTION:'
                contentE.innerText = req.description;
            }
    
            oldBody.parentNode.replaceChild(newBody, oldBody);

            console.log('Building complete!');
            resolve();
        });       
        
    },
    approve: function(caller, id) {
        detailsRowE = caller.parentNode.parentNode;
        descRowE = caller.parentNode.parentNode.nextSibling;

        detailsRowE.classList.add('resolved');
        descRowE.classList.add('resolved');
        
        // detailsRowE.addEventListener("transitionend", (e) => {
        //     setTimeout(() => {
        //         console.log("Removing rows");
        //         table = detailsRowE.parentNode;
        //         table.removeChild(detailsRowE);
        //         table.removeChild(descRowE);
        //     }, 100);         
        // }, {});

        post('../reimbursement/approve/' + id).then(() => {
            console.log("Hiding rows");
            detailsRowE.classList.add('out');
            descRowE.classList.add('out');
        });
    },

    deny: function(caller, id) {
        caller.parentNode.parentNode.classList.add('resolved');
        caller.parentNode.parentNode.nextSibling.classList.add('resolved');
        
        post('../reimbursement/deny/' + id).then(function() {
            console.log("Hiding rows");
            detailsRowE.classList.add('out');
            descRowE.classList.add('out');
        });
    }


}