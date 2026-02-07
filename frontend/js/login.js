const address = "127.0.0.1:420";

function onButtonLoginClick(){
    const username = document.getElementById("inputEmail").value;
    const password = document.getElementById("inputPassword").value;

    fetch(address + "/fW6zTqJ0nPBmKv19aXcdLryOUE38gZsj",{
         method: "POST",    
         headers:{ "Content-Type":"application/json"},
         body: JSON.stringify({ 
            inputUsername: username,
            inputPassword: password
         })

    });
}