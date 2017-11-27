directory = {
    history: document.getElementById('request-history'),
    create:  document.getElementById('create-request'),
    audit:   document.getElementById('audit-request'),
    
    toRequestHistory: () => {
        console.log("Going to history");
        view.swapView(view.directoryView, view.expenseView);
    },

    toCreateRequest: () => {

    },

    toAuditRequests: () => {
        if(user.status == "EMPLOYEE") return;


    },

    fetchRequestHistory: () => {
        let request = new XMLHttpRequest();

        on 

                
    }

}