function onButtonLoginClick(){
    const username = document.getElementById("inputUsername").value;
    const password = document.getElementById("inputPassword").value;

    fetch("/fW6zTqJ0nPBmKv19aXcdLryOUE38gZsj",{
         method: POST,
         headers:{ "Content-Type":"application/json"},
         body: JSON.stringify({ 
            inputUsername: username,
            inputPassword: password
         })

    });
}