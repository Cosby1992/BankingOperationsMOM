
var selected;


function selectLoan(element) {

    // console.log(element.children);

    const leftContent = document.getElementById("left-content");
    const rightContent = document.getElementById("right-content");

    let existsInRightContent = false;

    let cards = rightContent.getElementsByClassName('loan-card');

    for (let i = 0; i < cards.length; i++) {
        if(cards[i] == element) existsInRightContent = true;
    }

    if(existsInRightContent) {
        leftContent.appendChild(element)
        selected = false;
        console.log("Nothing selected")
        document.getElementById('apply-for-loan-button').classList.add("hidden");

    } else {
        for (let i = 0; i < cards.length; i++) {
            leftContent.appendChild(cards[i]);
        }

        selected = element;
        rightContent.appendChild(element)
        console.log(selected.getElementsByClassName("card-text-header")[1].innerHTML);
        document.getElementById('apply-for-loan-button').classList.remove("hidden");

        setFormElements();

    }

}

function setFormElements() {
    document.getElementById("bankname").value = selected.getElementsByClassName("card-text-header")[1].innerHTML;
    document.getElementById("costprmonth").value = selected.getElementsByClassName("card-text-header")[7].innerHTML;
    document.getElementById("apr").value = selected.getElementsByClassName("card-text-header")[3].innerHTML;
    document.getElementById("totalcost").value = selected.getElementsByClassName("card-text-header")[5].innerHTML;
    document.getElementById("paybackperiod").value = selected.getElementsByClassName("card-text-header")[9].innerHTML;
}



