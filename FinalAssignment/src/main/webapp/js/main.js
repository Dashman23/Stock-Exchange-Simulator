let callURL = "http://localhost:8080/lab6-1.0/api/students/json";

function requestData(callURL){
    fetch(callURL, {
        method: 'GET',
        headers: {
            'Accept': 'application/json',
        },
    })
        .then(response => response.json())
        // .then(response => add_record_API("chart", response)) TODO Function that uses response data
        .catch((err) => {
            console.log("something went wrong: " + err);
        });
}