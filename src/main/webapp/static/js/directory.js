directory = {
    history: document.getElementById('request-history'),
    create:  document.getElementById('create-request'),
    audit:   document.getElementById('audit-request'),
    
    toRequestHistory: () => {
        console.log("Going to history");
        hideCreate();
        view.swapView(view.directoryView, view.expenseView);
    },

    toCreateRequest: () => {
        console.log("Going to create request");
        showCreate();
        view.swapView(view.directoryView, view.expenseView);
    },

    toAuditRequests: () => {
        if(user.status == "EMPLOYEE") return;
        //go loading
        view.showLoading();

        //get pending reimb
        //audit.fetchPending().then(function(data) {
        post('../reimbursement/pending').then(function(data) {
            console.log('Data retrieved!');
            audit.pending = JSON.parse(data);
            return audit.update();
        }).then(function(data) {
            //transition
            view.swapView(view.directoryView, view.auditView);
            //hide loading
            view.hideLoading();
        });

        
    }


}