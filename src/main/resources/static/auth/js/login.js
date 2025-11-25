document.addEventListener('DOMContentLoaded',()=>{


const tabs = document.querySelectorAll('.tab');
const forms = document.querySelectorAll('.form');
const goSignup = document.getElementById('go-signup');
const goLogin = document.getElementById('go-login');

function showForm(type){
    forms.forEach(form=>{
        form.classList.toggle('active',form.id === `${type}-form`);
    });

    tabs.forEach(tab=>{
        tab.classList.toggle('active',tab.dataset.target === type);

    });

    // update welcome text depending on active form
    updateWelcome(type);
}

tabs.forEach(tab=>{
    tab.addEventListener('click',()=>{showForm(tab.dataset.target)});
});
// goSignup.addEventListener('click',()=>{showForm('signup')});
// goLogin.addEventListener('click',()=>{showForm('login')});


forms.forEach(form=>{
    form.addEventListener('submit',(e)=>{
        e.preventDefault();
        alert(`${form.id.replace('-form','')} form submitted!`);
    });
});


function updateWelcome(type){

    const heroHeading = document.querySelector('.hero-title h1');
    if(!heroHeading) return;
    const welcomeMessages = {
        signup : 'Welcome Aboard!',
        login : 'Welcome Back!'
    };
    heroHeading.innerHTML = welcomeMessages[type] || '';
}
//password show garne
const passwordInput = document.getElementById('password');

const eyeClose = document.querySelector('.eye-icon.closed');
const eyeOpen = document.querySelector('.eye-icon.open');

eyeClose.addEventListener('click',()=>{
    passwordInput.type = 'text';
    eyeClose.classList.remove('active');
    eyeOpen.classList.add('active');
});
eyeOpen.addEventListener('click',()=>{
    passwordInput.type = 'password';
    eyeClose.classList.add('active');
    eyeOpen.classList.remove('active');
});


//fetch garne verifcatoin code button

const verifyEmailBtn = document.getElementById('verify-email-btn');
const emailInput = document.getElementById('email');
const otpInputs = document.querySelectorAll('.otp-input');
const hiddenOtpInput = document.getElementById('verification-code');

 //send code when clicking send  code
verifyEmailBtn.addEventListener('click',async()=>{
    const email = emailInput.value.trim();
    if(!email){
        alert('Please enter a valid email address.'); //eslai paxi toast garna parxa
        return;
    }
//     //simulate sending code
    try{
        const response = await fetch('/api/send-verification-code',{ // eslai paxi backend sanga connect garna parxa
            method:'POST',
            headers:{'Content-Type':'application/json'},
            body:JSON.stringify({email})
        });
        if(!response.ok) throw new Error('Network response was not ok');
    }catch(error){
        console.error('Error sending verification code:',error);
        alert('Failed to send verification code. Please try again later.');
        return;
    }
});

otpInputs.forEach((input,index)=>{
    input.addEventListener('input',()=>{
        if(input.value && index < otpInputs.length - 1){
            otpInputs[index + 1].focus();
        }
    });

    input.addEventListener('keydown',(e)=>{
        if(e.key ==='Backspace' && !input.value && index>0){
            otpInputs[index -1 ].focus();
        }
    })

});




// functionality for the button and signup

const signUpForm = document.getElementById('signup-form');
const steps = signUpForm.querySelectorAll('.step');
const nextBtn = document.getElementById('next-btn');
const prevBtn = document.getElementById('prev-btn');

let currentStep = 1;

function updateStepUI(){
    //show current step
     steps.forEach((step, index) => {
    step.style.display = (index + 1 === currentStep) ? "block" : "none";
  });

    prevBtn.innerHTML = "<i class = 'bx bx-left-arrow-alt'></i> <span>Back</span>";
    //change button if step is at last which is 3
   if (currentStep === 3) {
        nextBtn.innerHTML = `<span>Create Account</span>`;
    } else {
        nextBtn.innerHTML = `<span>Next</span><i class='bx bx-right-arrow-alt'></i>`;
    }
    if(currentStep === 2){
        nextBtn.innerHTML = `<span>Verify</span>`
    }


    //hide prev button if step is first
    prevBtn.style.display = (currentStep === 1 )? "none" : "";

}
//calling once to set initial state
updateStepUI();

//button click events

//when on step 2 verify code
nextBtn.onclick = async ()=>{
//    if (currentStep === 2) {
//     const code = Array.from(otpInputs)
//       .map(input => input.value.trim())
//       .join('');

//     if (code.length !== otpInputs.length) {
//       alert("Please enter the full verification code.");
//       return;
//     }

//     // store in hidden input for final submit
//     hiddenOtpInput.value = code;
//     try{
//         const res = await fetch('/api/verify-code',{
//             method:'POST',
//             headers:{'Content-Type':'application/json'},
//             body:JSON.stringify({
//                 email:emailInput.value.trim(),
//                 code
//             })
//     });
//     if(!res.ok) throw new Error('Network response was not ok');
//     const data = await res.json();
//     if(!data.valid){
//         alert('Invalid verification code. Please try again.');
//         return;
//     }
// }catch(error){
//     console.error('Error verifying code:',error);
//     alert('Failed to verify code. Please try again later.');
//     return;
// }

    if(currentStep<3){
        currentStep++;
        updateStepUI( );
    }else{
        signUpForm.submit();
    }
};
prevBtn.onclick = ()=>{
    if(currentStep>1){
        currentStep--;
        updateStepUI( );
    }
};

});