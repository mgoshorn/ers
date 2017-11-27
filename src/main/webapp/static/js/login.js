let login = function() {

    let getUsername = function() {
        return document.getElementById('username').value;
    }

    let getPassword = function() {
        return document.getElementById('password').value
    }

    let createBody = function() {
        body = {
            username: getUsername(),
            password: getPassword()
        }
        return JSON.stringify(body);
    }

    // let loginPromise = new Promise(function(resolve, reject) {
    //     view.showLoading();
    //     let request = new XMLHttpRequest();


    // })

    let loginPromise = function() {
        message = createBody();
        console.log(message);
        post('../user/login', message).then(function(data) {
            user.setup(JSON.parse(data))
            console.log(data);
            return post('../reimbursement');
        }).then(function(data) {
            console.log("Second then!");
            console.log(data);
            user.reimbursements.setup(data);
            login.transitionOut();
        }).catch(function(error) {
            console.log(error);  
        });
        
    }

    let pLogin = function() {
        view.showLoading();
        message = createBody();
        console.log(message);
        post('../user/login', message).then(function(data) {
            console.log(data);
            userSetup(JSON.parse(data))
            return post('../reimbursement', undefined);
        }).then(function(data) {
            //console.log(data);
            reimbursementsSetup(data);
            userbar.update();
            view.directory.update();
            view.reimbursements.update();
            view.swapView(view.loginView, view.directoryView);
            view.showUserbar();
            view.hideLoading();
        }).catch(function(error) {
            console.log(error);
            view.hideLoading();
            //TODO error prompt
        });
    }

    let send = function(json) {
        view.showLoading();
        let request = new XMLHttpRequest();

        request.addEventListener('progress', (e) => {
            console.log('progression');
        });

        request.addEventListener('load', (e) => {
            console.log('loaded');
            let data = JSON.parse(request.responseText);
            if(request.status === 401) {
                //TODO show username or password incorrect alert

            }

            if(request.status === 303) {
                //TODO Clear alerts

                //Create user object
                user = new createUser();
                user.setUsername(data.username);
                user.setFirstName(data.firstName);
                user.setLastName(data.lastName);
                user.setEmail(data.email);

                //Transition to directory
                console.log('transitioning...');
                userbar.update();
                view.directory.update();
                view.swapView(view.loginView, view.directoryView);
                view.showUserbar();
                view.hideLoading();

            }
        });

        request.addEventListener('error', (e) => {
            console.log('error');
            view.hideLoading();
        })

        request.addEventListener('abort', (e) => {
            console.log('abort');
            view.hideLoading();
        })

        console.log("Changes are working.");

        request.open('POST', '../user/login', true);
        request.send(json);

        console.log(request);
        
    }

    let failure = function() {
        console.log("Login failed.");
    }
    pLogin();

    // username = getUsername();
    // password = getPassword();

    // json = createBody(username, password);
    // send(json);
}