function createUser() {
    this.username;
    this.firstName;
    this.lastName;
    this.email;
    this.role;
    this.requestHistory;
    this.reimbursements;

    this.setUsername = (str) => {
        this.username = str;
    }

    this.setFirstName = (str) => {
        this.firstName = str;
    }

    this.setLastName = (str) => {
        this.lastName = str;
    }

    this.setEmail = (str) => {
        this.email = str;
    }

    this.setRole = (str) => {
        this.role = str;
    }

    this.getUsername = () => {
        return this.username;
    }

    this.getFirstName = () => {
        return this.firstName;
    }

    this.getLastName = () => {
        return this.lastName;
    }

    this.getEmail = () => {
        return this.email;
    }

    this.getRole = () => {
        return this.role;
    }

    this.getRequestHistory = () => {
        return this.requestHistory;
    }

    this.fetchRequestHistory = () => {
        
    }
}

let userbar = {
    update: () => {
        nameTag = document.getElementById("userbar-username");
        nameTag.innerText = (user.getFirstName() + " " + user.getLastName());
    },
    show: () => {
        element = document.getElementById('userbar');
        element.classList.remove('hidden');
        setTimeout(e => {
            element.classList.remove('invis');
        }, 50);
    }

}

userSetup = function(uObj) {
    user = new createUser();
    user.setUsername(uObj.username);
    user.setFirstName(uObj.firstName);
    user.setLastName(uObj.lastName);
    user.setEmail(uObj.email);
    user.setRole(uObj.role);
    console.log("User setup!");
}

reimbursementsSetup = function(data) {
    console.log(data);
    user.reimbursements = JSON.parse(data);
}

let user;