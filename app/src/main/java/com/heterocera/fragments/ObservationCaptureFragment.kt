package com.heterocera.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.location.*
import com.heterocera.R
import com.heterocera.database.HeteroceraDatabase
import com.heterocera.database.Repository
import com.heterocera.viewmodel.MothViewModel
import com.heterocera.viewmodel.MothViewModelFactory
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.HashMap


open class ObservationCaptureFragment : Fragment() {
    private lateinit var activityContext: Context
    //set up the database
    val database by lazy { HeteroceraDatabase.getDatabase(activityContext)}
    private val repository by lazy { Repository(database.encyclopediaDAO(),database.observationDAO()) }
    //s et up a view model
    private val mothViewModel: MothViewModel by viewModels {
        MothViewModelFactory(repository)
    }
    private val args : ObservationCaptureFragmentArgs by navArgs()
    private lateinit var fragManager: FragmentManager

    // bunch of variables that are going to be passed to an observation entry
    private var observationTimestamp = 0L
    private var observationLatitude: Double = 0.0
    private var observationLongitude = 0.0
    private var dateTimeStampString = ""
    private var locationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            super.onLocationResult(p0)
            currentLocation = p0.lastLocation
            // PUT THE UI CODE HERE
            Log.d("LocationResult",p0.lastLocation.toString())
            observationLatitude = currentLocation!!.latitude
            observationLongitude = currentLocation!!.longitude
            observationTimestamp = currentLocation!!.time
            val instant = Instant.ofEpochMilli(p0.lastLocation!!.time)
            val dateTimeStamp = OffsetDateTime.ofInstant(instant, ZoneId.of("GMT+1"))
            dateTimeStampString =
                dateTimeStamp.format(DateTimeFormatter.ISO_DATE_TIME).toString()
            rootView.findViewById<TextView>(R.id.date_text_view).text = dateTimeStampString
            Log.d("NullCheck","Passed?")
            val latLngString = buildString {
                append("Latitude: ")
                append(observationLatitude.toString())
                append("\n Longitude: ")
                append(observationLongitude.toString())
            }
        rootView.findViewById<TextView>(R.id.latlng_text_view).text = latLngString
        }
    }
    private var currentLocation: Location? = null
    private lateinit var navigationController:NavController
    private lateinit var rootView:View
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_observation_capture, container, false)
        return rootView
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Toast.makeText(activityContext,"Getting your location data ... Please wait",Toast.LENGTH_LONG).show()
        LocationServices.getFusedLocationProviderClient(activityContext).lastLocation
            .addOnSuccessListener {
                Toast.makeText(activityContext,"Successfully found Geo coordinates.",Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(activityContext,"Failed to find geographical coordinates. " +
                        "You can edit your observation manually in the observations screen. ",
                        Toast.LENGTH_LONG).show()
            }
        val imgMap:HashMap<String,String> = HashMap(100)
        imgMap["angle_shades"] = "Angle Shades"
        imgMap["barred_straw"] = "Barred Straw"
        imgMap["black_rustic"] = "Black Rustic"
        imgMap["blairs_shoulder_knot"] = "Blairs Shoulder Knot"
        imgMap["bordered_white"] = "Bordered white"
        imgMap["bright_line_brown_eye"] = "Bright-line brown-eye"
        imgMap["brimstone_moth"] = "Brimstone"
        imgMap["broad_barred_white"] = "Broad-barred white"
        imgMap["brown_china-mark"] = "Brown China-mark"
        imgMap["brown_silver-line"] = "Brown silver-line"
        imgMap["burnished_brass"] = "Burnished Brass"
        imgMap["chestnut"] = "Chestnut"
        imgMap["cinnabar"] = "Cinnabar"
        imgMap["clouded_border"] = "Clouded Border"
        imgMap["clouded_bordered_brindle"] = "Clouded bordered-brindle"
        imgMap["clouded_buff"] = "Clouded Buff"
        imgMap["clouded_silver"] = "Clouded Silver"
        imgMap["common_carpet"] = "Common Carpet"
        imgMap["common_emerald"] = "Common Emerald"
        imgMap["common_heath"] = "Common Heath"
        imgMap["common_plume"] = "Common Plume"
        imgMap["common_wave"] = "Common Wave"
        imgMap["common_white_wave"] = "Common white wave"
        imgMap["early_grey"] = "Early grey"
        imgMap["elephant_hawk_moth"] = "Elephant Hawk Moth"
        imgMap["feathered_thorn"] = "Feathered Thorn"
        imgMap["garden_carpet"] = "Garden Carpet"
        imgMap["garden_tiger"] = "Garden Tiger"
        imgMap["gold_spot"] = "Gold spot"
        imgMap["grass_emerald"] = "Grass Emerald"
        imgMap["green-brindled_crescent"] = "Green-Brindled crescent"
        //deliberately misspelled, don't change it
        imgMap["green_carept"] = "Green carpet"
        imgMap["heart_and_dart"] = "Heart and Dart"
        imgMap["hummingbird_hawkmoth"] = "Hummingbird Hawkmoth"
        imgMap["latticed_heath_chiasmia"] = "Latticed Heath Chiasmia"
        imgMap["magpie"] = "Magpie"
        imgMap["merveille_du_jour"] = "Merveille du jour"
        imgMap["mother_of_pearl"] = "Mother of Pearl"
        imgMap["mother_shipton"] = "Mother Shipton"
        imgMap["muslin_moth"] = "Muslin MOth"
        imgMap["nut-tree_tussock"] = "Nut-tree tussock"
        imgMap["pale_shouldered_brocade"] = "Pale Shouldered brocade"
        imgMap["pine_beauty"] = "Pine Beautry"
        imgMap["pink_barred_sallow"] = "Pink Barred Sallow"
        imgMap["poplar_hawk_moth"] = "Poplar hawkmoth"
        imgMap["pussmoth"] = "Pussmoth"
        imgMap["red-necked_footman"] = "Red-necked footman"
        imgMap["ruby_tiger"] = "Ruby TIger"
        imgMap["scalloped_oak"] = "Scalloped Oak"
        imgMap["setaceous_hebrew_character"] = "Setaceous Hebrew Character"
        imgMap["shaded_broad_bar"] = "Shaded Broad bar"
        imgMap["shark"] = "Shark"
        imgMap["shears"] = "Shears"
        imgMap["silver-ground_carpet"] = "Silver-ground carpet"
        imgMap["silver-y_moth"] = "Silver-y moth"
        imgMap["six_spot_burnet"] = "Six Spot Burnet"
        imgMap["small_emperor_moth"] = "Small Emperor Moth"
        imgMap["small_square_spot"] = "Small Square Spot"
        imgMap["smoky_wainscot"] = "Smoky Wainscot"
        imgMap["square_spot_rustic"] = "Square Spot Rustic"
        imgMap["straw_dot"] = "Straw dot"
        imgMap["swallow_tailed_moth"] = "Swallow Tailed MOth"
        imgMap["timothy_tortrix"] = "Timothy Tortrix"
        imgMap["true_lovers_knot"] = "True lovers knot"
        imgMap["udea_lutealis"] = "Udea Lutealis"
        imgMap["white_ermine"] = "White Ermine"
        imgMap["white_plume"] = "White Plume"
        imgMap["willow_beauty"] = "Willow Beauty"
        imgMap["yellow_shell"] = "Yellow Shell"
        super.onViewCreated(view, savedInstanceState)
        var selectedMoth: String = args.recommendedLabel
        Log.d("ObservationCapture - filename toString",args.filename.toString())
        class SpinnerActivity : Activity(), AdapterView.OnItemSelectedListener {
            // this is a private class designed to tell what the spinner item
            // the choice of moth in the observation to do
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                selectedMoth = parent.getItemAtPosition(pos).toString()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
        val spinner : Spinner = view.findViewById(R.id.common_name_spinner)
        // UUID isn't the most space efficient way to uniquely identify something
        // but it is, for most intents and purposes, guaranteed
        val uuid: UUID = UUID.randomUUID()
        val highestMothMatchTextView : TextView = view.findViewById(R.id.suggestedTextView)
        // old saw from java
        // string builder is more efficent than string concatenation
        highestMothMatchTextView.text = buildString {
            append("Suggested species match: ")
            append(args.recommendedLabel)
        }
        val inStream = activityContext.contentResolver.openInputStream(args.filename)
        val mothBitmap = properRotateBitmap(BitmapFactory.decodeStream(inStream))
        val observationCaptureImageView: ImageView = view.findViewById(R.id.observation_capture_view)
        observationCaptureImageView.setImageBitmap(mothBitmap)
        val mothSpeciesListAdapter = ArrayAdapter(activityContext,R.layout.moth_spinner_item,
            mothViewModel.mothArray).also {adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }
        spinner.adapter = mothSpeciesListAdapter
        spinner.onItemSelectedListener = SpinnerActivity()
        val descriptionTextView: TextView = view.findViewById(R.id.observation_description)
        val captureFormalName = runBlocking {
            imgMap[selectedMoth]?.let { repository.getFormalFromCommon(it) }
        }
        view.findViewById<Button>(R.id.submit_observation).setOnClickListener{
            selectedMoth = selectedMoth.replace("_"," ")
            if (captureFormalName != null) {
                // write the observation
                Log.d("Write Observation","Writing")
                submitObservation(
                    observationLongitude,
                    selectedMoth,
                    descriptionTextView,
                    observationLatitude,
                    observationTimestamp,
                    captureFormalName,
                    uuid
                )
            }
            Toast.makeText(activityContext,"Observation recorded!",Toast.LENGTH_SHORT).show()
            // slightly unwieldly way, avoids having to declare an entirely new nav graph
            fragManager.popBackStack()
            navigationController.navInflater.inflate(R.navigation.nav_graph)
                .setStartDestination(R.id.cameraFragment)
            navigationController.graph =
                navigationController.navInflater.inflate(R.navigation.nav_graph)
        }
    }


    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
            val locationRequest = LocationRequest.Builder(10)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY).setMinUpdateDistanceMeters(5F).build()
            LocationServices.getFusedLocationProviderClient(activityContext)
                .requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())

    }
    private fun properRotateBitmap(source:Bitmap) : Bitmap {
        val matrix = Matrix()
        matrix.postRotate(90F)
        val bitmap =  Bitmap.createBitmap(source,0,0,source.width,source.height,matrix,true)
        source.recycle()
        return bitmap
    }
    private fun submitObservation(observationLongitude:Double,selectedMoth:String,
                                  observationDescription:TextView, observationLatitude:Double
                                  ,observationTimestamp:Long,captureFormalName:String
                                  ,uuid:UUID) {
        //shim to the database function call
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                repository.writeObservation(
                    obs_long = observationLongitude,
                    obs_comm = selectedMoth,
                    obs_desc = observationDescription.text.toString(),
                    obs_time = observationTimestamp,
                    obs_formal = captureFormalName,
                    obs_lat = observationLatitude,
                    uuid = uuid.toString(),
                    obs_image_filename = args.filename.toString()
                )
                fragManager.popBackStack()

            }
        }
    }


    @SuppressLint("MissingPermission")
    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.activityContext = context
        fragManager = activity?.supportFragmentManager!!
        this.navigationController = this.findNavController()
        activityContext.theme.applyStyle(R.style.Theme_Heterocera,true)


    }
}