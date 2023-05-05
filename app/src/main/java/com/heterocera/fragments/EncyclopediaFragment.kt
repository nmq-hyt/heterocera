package com.heterocera.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.heterocera.R
import com.heterocera.adapters.MothListAdapter
import com.heterocera.database.HeteroceraDatabase
import com.heterocera.database.MothSpecies
import com.heterocera.database.Repository
import com.heterocera.databinding.FragmentEncyclopediaBinding
import com.heterocera.viewmodel.MothViewModel
import com.heterocera.viewmodel.MothViewModelFactory


class EncyclopediaFragment : Fragment() {

    private lateinit var mothListAdapterReference: MothListAdapter
    private lateinit var activityContext: Context
    val database by lazy { HeteroceraDatabase.getDatabase(activityContext)}
    private val repository by lazy { Repository(database.encyclopediaDAO(),database.observationDAO()) }
    private val mothViewModel: MothViewModel by viewModels {
        MothViewModelFactory(repository)
    }
    private val binding get() = _binding!!
    private val imgMap: HashMap<String,Int> = HashMap(70)
    private lateinit var recyclerView : RecyclerView
    private var _binding : FragmentEncyclopediaBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // using a hash map is techincally a little inefficient
        // because you must create a seperate entry for each key,value pair
        // but it'll do for the moment
        // although ideally it should be replaced with a more efficent mapping structure
        imgMap["angle_shades"] = R.drawable.angle_shades
        imgMap["barred_straw"] = R.drawable.barred_straw
        imgMap["black_rustic"] = R.drawable.black_rustic
        imgMap["blairs_shoulder_knot"] = R.drawable.blairs_shoulder_knot
        imgMap["bordered_white"] = R.drawable.bordered_white
        imgMap["bright_line_brown_eye"] = R.drawable.bright_line_brown_eye
        imgMap["brimstone_moth"] = R.drawable.brimstone_moth
        imgMap["broad_barred_white"] = R.drawable.broad_barred_white
        imgMap["brown_china-mark"] = R.drawable.brown_china_mark
        imgMap["brown_silver-line"] = R.drawable.brown_silver_line
        imgMap["burnished_brass"] = R.drawable.burnished_brass
        imgMap["chestnut"] = R.drawable.chestnut
        imgMap["cinnabar"] = R.drawable.cinnabar
        imgMap["clouded_border"] = R.drawable.clouded_border
        imgMap["clouded_bordered_brindle"] = R.drawable.clouded_bordered_brindle
        imgMap["clouded_buff"] = R.drawable.clouded_buff
        imgMap["clouded_silver"] = R.drawable.clouded_silver
        imgMap["common_carpet"] = R.drawable.common_carpet
        imgMap["common_emerald"] = R.drawable.common_emerald
        imgMap["common_heath"] = R.drawable.common_heath
        imgMap["common_plume"] = R.drawable.common_plume
        imgMap["common_wave"] = R.drawable.common_wave
        imgMap["common_white_wave"] = R.drawable.common_white_wave
        imgMap["early_grey"] = R.drawable.early_grey
        imgMap["elephant_hawk_moth"] = R.drawable.elephant_hawk_moth
        imgMap["feathered_thorn"] = R.drawable.feathered_thorn
        imgMap["garden_carpet"] = R.drawable.garden_carpet
        imgMap["garden_tiger"] = R.drawable.garden_tiger
        imgMap["gold_spot"] = R.drawable.gold_spot
        imgMap["grass_emerald"] = R.drawable.grass_emerald
        imgMap["green-brindled_crescent"] = R.drawable.green_brindled_crescent
        //deliberately misspelled, don't change it
        imgMap["green_carept"] = R.drawable.green_carpet
        imgMap["heart_and_dart"] = R.drawable.heart_and_dart
        imgMap["hummingbird_hawkmoth"] = R.drawable.hummingbird_hawkmoth
        imgMap["latticed_heath_chiasmia"] = R.drawable.latticed_heath_chiasmia
        imgMap["magpie"] = R.drawable.magpie
        imgMap["merveille_du_jour"] = R.drawable.merveille_du_jour
        imgMap["mother_of_pearl"] = R.drawable.mother_of_pearl
        imgMap["mother_shipton"] = R.drawable.mother_shipton
        imgMap["muslin_moth"] = R.drawable.muslin_moth
        imgMap["nut-tree_tussock"] = R.drawable.nut_tree_tussock
        imgMap["pale_shouldered_brocade"] = R.drawable.pale_shouldered_brocade
        imgMap["pine_beauty"] = R.drawable.pine_beauty
        imgMap["pink_barred_sallow"] = R.drawable.pink_barred_sallow
        imgMap["poplar_hawk_moth"] = R.drawable.poplar_hawk_moth
        imgMap["pussmoth"] = R.drawable.pussmoth
        imgMap["red-necked_footman"] = R.drawable.red_necked_footman
        imgMap["ruby_tiger"] = R.drawable.ruby_tiger
        imgMap["scalloped_oak"] = R.drawable.scalloped_oak
        imgMap["setaceous_hebrew_character"] = R.drawable.setaceous_hebrew_character
        imgMap["shaded_broad_bar"] = R.drawable.shaded_broad_bar
        imgMap["shark"] = R.drawable.shark
        imgMap["shears"] = R.drawable.shears
        imgMap["silver-ground_carpet"] = R.drawable.silver_ground_carpet
        imgMap["silver-y_moth"] = R.drawable.silver_y_moth
        imgMap["six_spot_burnet"] = R.drawable.six_spot_burnet
        imgMap["small_emperor_moth"] = R.drawable.small_emperor_moth
        imgMap["small_square_spot"] = R.drawable.small_square_spot
        imgMap["smoky_wainscot"] = R.drawable.smoky_wainscot
        imgMap["square_spot_rustic"] = R.drawable.square_spot_rustic
        imgMap["straw_dot"] = R.drawable.straw_dot
        imgMap["swallow_tailed_moth"] = R.drawable.swallow_tailed_moth
        imgMap["timothy_tortrix"] = R.drawable.timothy_tortrix
        imgMap["true_lovers_knot"] = R.drawable.true_lovers_knot
        imgMap["udea_lutealis"] = R.drawable.udea_lutealis
        imgMap["white_ermine"] = R.drawable.white_ermine
        imgMap["white_plume"] = R.drawable.white_plume
        imgMap["willow_beauty"] = R.drawable.willow_beauty
        imgMap["yellow_shell"] = R.drawable.yellow_shell
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.activityContext = context
        activityContext.theme.applyStyle(R.style.Theme_Heterocera,true)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // there are two search views in the SDK, and it doesn't appear
        // to clearly delineate them - this is the "correct" one, referring to a local searchbar
        val searchView =
            view.findViewById<androidx.appcompat.widget.SearchView>(R.id.encyclopedia_search_bar)

        recyclerView  = view.findViewById(R.id.encyclopediaFragmentRecyclerView)
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            mothViewModel.alphabetizedMothSpecies.collect {value: List<MothSpecies> ->
                val mothListAdapter = MothListAdapter(value,imgMap)
                mothListAdapterReference = mothListAdapter
                recyclerView.adapter = mothListAdapter

            }
        }
        // query text listener meaning "whenever you type something in"
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextChange(newText: String?): Boolean {
                mothListAdapterReference.filter.filter(newText)
                recyclerView.adapter=mothListAdapterReference
                return true
            }
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
        })
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentEncyclopediaBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
