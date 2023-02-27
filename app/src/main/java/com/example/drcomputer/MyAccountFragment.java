package com.example.drcomputer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;


public class MyAccountFragment extends Fragment {
    private FloatingActionButton editProfileDetails;
    private View view;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private TextView myAccountUserName;
    private TextView myAccountEmail;
    private TextView userFullName;
    private TextView userMobNo;
    private TextView userLocation;
    private TextView userCity;
    private TextView userState;
    private StorageReference firebaseStorage;
    private TextView userPincode;
    private Button myAccountSignoutBtn;
    private String userID;
    private CircleImageView profilePhoto;
    private Uri imageUri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_my_account, container, false);
        editProfileDetails = view.findViewById(R.id.my_account_edit_btn);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance().getReference();
        userFullName = view.findViewById(R.id.my_account_fullname);
        userMobNo = view.findViewById(R.id.my_account_mobile);
        myAccountUserName = view.findViewById(R.id.my_account_user_name);
        myAccountEmail = view.findViewById(R.id.my_account_user_email);
        userLocation = view.findViewById(R.id.my_account_location);
        userCity = view.findViewById(R.id.my_account_city);
        userState = view.findViewById(R.id.my_account_state);
        userPincode = view.findViewById(R.id.my_account_pincode);
        myAccountSignoutBtn = view.findViewById(R.id.my_account_sign_out_btn);
        profilePhoto = view.findViewById(R.id.my_account_profile_photo);

        if (firebaseUser!=null){
            firebaseFirestore.collection("USERS").whereEqualTo("email",firebaseUser.getEmail()).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()){
                                for (QueryDocumentSnapshot documentSnapshot : task.getResult()){
                                    Glide.with(getActivity()).load(documentSnapshot.get("profileImage")).apply(new RequestOptions().placeholder(R.drawable.my_account_circle_view)).into(profilePhoto);
                                    userFullName.setText(documentSnapshot.get("fullname").toString());
                                    userMobNo.setText(documentSnapshot.get("mobno").toString());
                                    userLocation.setText(documentSnapshot.get("location").toString());
                                    userCity.setText(documentSnapshot.get("city").toString());
                                    userState.setText(documentSnapshot.get("state").toString());
                                    userPincode.setText(documentSnapshot.get("pincode").toString());
                                    myAccountEmail.setText(documentSnapshot.get("email").toString());
                                    myAccountUserName.setText(documentSnapshot.get("fullname").toString());
                                    userID = documentSnapshot.getId();
                                }
                            }
                        }
                    });
        }

        editProfileDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent updateDetails = new Intent(getActivity(),MyAccountUpdateDetailsActivity.class);
                startActivity(updateDetails);
            }
        });

        myAccountSignoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent register = new Intent(getActivity(),Login_Activity.class);
                register.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                register.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(register);
            }
        });

        profilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePicture();
            }
        });

        return view;
    }

    private void choosePicture(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1 && resultCode==RESULT_OK && data.getData()!=null){
            imageUri = data.getData();
            profilePhoto.setImageURI(imageUri);
            uploadPicture(); 
        }
    }

    private void uploadPicture() {

        if(imageUri != null) {
            StorageReference filePath = FirebaseStorage.getInstance().getReference("images").child(System.currentTimeMillis() + "." + getFileExtension(imageUri));

            StorageTask uploadTask = filePath.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    Uri downloadUri = task.getResult();
                    String imgUrl = downloadUri.toString();
                    firebaseFirestore.collection("USERS").document(userID).update("profileImage",imgUrl)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(getActivity(), "Image Uploaded Successfully", Toast.LENGTH_LONG).show();
                                    }
                                    else {
                                        Toast.makeText(getActivity(), "Failed to Upload", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            });
        }
    }

    private String getFileExtension(Uri uri) {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(getActivity().getContentResolver().getType(uri));
    }
}