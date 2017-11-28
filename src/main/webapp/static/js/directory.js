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


    }
}