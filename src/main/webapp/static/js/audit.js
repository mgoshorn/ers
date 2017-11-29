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
                approveE.setAttribute('onclick', 'console.log("Test")');

                denyE.classList.add('btn');
                denyE.classList.add('btn-warning');
                denyE.innerText = 'Deny';

                //append buttons
                auditE.appendChild(approveE);
                auditE.appendChild(denyE);


                //append onclick
                //TODO
    
                //create anchor
                let receiptLinkE = document.createElement('a');
                receiptLinkE.href = `../reimbursement/receipt_${req.id}`;
                receiptLinkE.innerText = 'receipt';
                receiptE.appendChild(receiptLinkE);
    
                amountE.innerText = req.amount.toFixed(2);
                requesterE.innerText = req.author.firstName + ' ' + req.author.lastName;
                d = req.submitted;
                requestedE.innerText = d.dayOfWeek + ', ';
                requestedE.innerText+= d.month + ' ' + d.dayOfMonth + ' ';
                requestedE.innerText+= d.hour + ':' + d.minute + ':' + d.second + ' (' + d.year + ')';
                //Monday, November 22 12:00:00 (2017) 
                typeE.innerText = req.type;
                titleE.innerText = 'DESCRIPTION:'
                contentE.innerText = req.description;
            }
    
            oldBody.parentNode.replaceChild(newBody, oldBody);

            console.log('Building complete!');
            resolve();

        });
        
        
    }


}