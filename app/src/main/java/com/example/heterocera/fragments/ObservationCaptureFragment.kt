package com.example.heterocera.fragments

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.heterocera.R
import com.example.heterocera.database.HeteroceraDatabase
import com.example.heterocera.database.Repository
import com.example.heterocera.viewmodel.MothViewModel
import com.example.heterocera.viewmodel.MothViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Tasks
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*


class ObservationCaptureFragment : Fragment() {


    private lateinit var activityContext: Context
    val database by lazy { HeteroceraDatabase.getDatabase(activityContext)}
    private val repository by lazy { Repository(database.encyclopediaDAO(),database.observationDAO()) }
    private val mothViewModel: MothViewModel by viewModels {
        MothViewModelFactory(repository)
    }
    private val args : ObservationCaptureFragmentArgs by navArgs()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var fragManager: FragmentManager

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.activityContext = context
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        fragManager = activity?.supportFragmentManager!!

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_observation_capture, container, false)
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var selectedMoth: String = "Moth"/*args.RecommendedLabel*/
        class SpinnerActivity : Activity(), AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                selectedMoth = parent.getItemAtPosition(pos) as String
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

        val spinner : Spinner = view.findViewById(R.id.common_name_spinner)
        val uuid:UUID = UUID.randomUUID()
        val highestMothMatchTextView : TextView = view.findViewById(R.id.suggestedTextView)
        highestMothMatchTextView.text = buildString {
        append("Suggested species match: ")
        append("moth"/*args.RecommendedLabel*/)
    }
//        val mothBitmap = BitmapFactory.decodeFile(args.filename)
        val observationCaptureImageView: ImageView = view.findViewById(R.id.observation_capture_view)
//        observationCaptureImageView.setImageBitmap(mothBitmap)
        val mothSpeciesListAdapter = ArrayAdapter(activityContext,android.R.layout.simple_spinner_item,
            mothViewModel.mothArray).also {adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }
        spinner.onItemSelectedListener = SpinnerActivity()
        spinner.adapter = mothSpeciesListAdapter
        val descriptionTextView: TextView = view.findViewById(R.id.observation_description)
        val observationDescription: TextView = descriptionTextView
        if (ActivityCompat.checkSelfPermission(
                activityContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                activityContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // This permission is already checked in my manifest, and should be granted at startup,
            // but better safe than sorry
            return
        }

        var observationLongitude = 0.0
        var observationLatitude = 0.0
        var observationTimestamp: Long = 0
        Tasks.await(fusedLocationClient.lastLocation.addOnSuccessListener{
            observationLongitude = it.longitude
            observationLatitude= it.latitude
            observationTimestamp= it.time
        })
        val instant = Instant.ofEpochMilli(observationTimestamp)
        val dateTimeStamp = OffsetDateTime.ofInstant(instant, ZoneId.of("GMT+1"))
        val dateTimeStampString = dateTimeStamp.format(DateTimeFormatter.ISO_DATE_TIME).toString()
        view.findViewById<TextView>(R.id.date_text_view).text = dateTimeStampString
        val observationLatLng = LatLng(observationLatitude,observationLongitude)
        val latLngString  = buildString {
            append("Latitude: ")
            append(observationLatLng.latitude.toString())
            append("\n Longitude: ")
            append(observationLatLng.longitude.toString())
        }
        view.findViewById<TextView>(R.id.latlng_text_view).text = latLngString
        val submitButton : ImageButton = view.findViewById(R.id.submit_observation)
        val captureFormalName = repository.getFormalFromCommon(selectedMoth)
        val submittedText = "Observation recorded!"
        val duration = Toast.LENGTH_SHORT
        val toast = Toast.makeText(activityContext,submittedText,duration)
        submitButton.setOnClickListener {
            repository.writeObservation(obs_long = observationLongitude, obs_comm =selectedMoth ,
                obs_desc = observationDescription.text.toString(), obs_time = observationTimestamp,
                obs_formal = captureFormalName.toString(), obs_lat = observationLatitude,
                uuid = uuid.toString())
            toast.show()
            val mapMarketBundle = Bundle()
            mapMarketBundle.putDouble("observationLatitude",observationLatitude)
            mapMarketBundle.putDouble("observationLongitude",observationLongitude)
            mapMarketBundle.putString("observationTimeStamp",dateTimeStampString)
            mapMarketBundle.putString("UUID",uuid.toString())
//            mapMarketBundle.putParcelable("image",mothBitmap)
            setFragmentResult("mapActivityRequestKey",mapMarketBundle)
            this.findNavController().navigate(R.id.action_observationCapture_to_cameraFragment)
        }

    }

}