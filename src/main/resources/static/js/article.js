// Delete
const deleteButton = document.getElementById('delete-btn');

if(deleteButton) {
    deleteButton.addEventListener('click', (e) => {
        let id = document.getElementById('article-id').value;
        fetch(`/api/articles/${id}`, {
            method: 'DELETE'
        })
            .then(() => {
                alert("successfully deleted!");
                location.replace('/articles');
            });
    });
}


// Update
const modifyButton = document.getElementById('modify-btn');

if(modifyButton) {
    modifyButton.addEventListener('click', (e) => {
        let params = new URLSearchParams(location.search);
        let id = params.get('id');

        fetch(`/api/articles/${id}`, {
            method: 'PUT',
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({
                title: document.getElementById('title').value,
                content: document.getElementById('content').value,
            })
        })
            .then(() => {
                alert("successfully modified!");
                location.replace(`/articles/${id}`);
            })
    })
}


// Register
const createButton = document.getElementById('create-btn');
if(createButton) {
    createButton.addEventListener('click', (e) => {
        fetch(`/api/articles`, {
            method: 'POST',
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({
                title: document.getElementById('title').value,
                content: document.getElementById('content').value,
            })
        })
            .then(()=>{
                alert("successfully created!");
                location.replace('/articles');
            })
    })
}