package proyectointegrador.madridindustria;

import android.content.Context;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;

public class FirebaseAuthHelper {

    private FirebaseAuth mAuth;
    private Context context;

    public FirebaseAuthHelper(Context context) {
        mAuth = FirebaseAuth.getInstance();
        this.context = context;
    }

    public void sendPasswordResetEmail(String email) {
        mAuth.sendPasswordResetEmail(email);
    }

    public void confirmEmail(String email, final OnCompleteListener<SignInMethodQueryResult> onCompleteListener) {
        mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(onCompleteListener);
    }

    public void createUserWithEmailAndPassword(String email, String password, OnCompleteListener<AuthResult> onCompleteListener) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(onCompleteListener);
    }

    public void signInWithEmailAndPassword(String email, String password, OnCompleteListener<AuthResult> onCompleteListener) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(onCompleteListener);
    }
}

