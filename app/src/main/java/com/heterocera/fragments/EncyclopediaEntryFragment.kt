package com.heterocera.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.heterocera.R
import com.heterocera.database.HeteroceraDatabase
import com.heterocera.database.Repository
import kotlinx.coroutines.runBlocking


class EncyclopediaEntryFragment : Fragment() {
    private lateinit var activityContext: Context
    val database by lazy { HeteroceraDatabase.getDatabase(activityContext)}
    private val repository by lazy { Repository(database.encyclopediaDAO(),database.observationDAO()) }
    private val args: EncyclopediaEntryFragmentArgs by navArgs()
    private val imgMap: HashMap<String,Int> = HashMap(70)


    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.activityContext = context
        activityContext.theme.applyStyle(R.style.Theme_Heterocera,true)


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_encyclopedia_entry, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val formalName = arguments?.getString("formal_name")

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {

            val moth = formalName?.let { repository.getMothFromFormal(it) }
            if (moth != null) {
                val mothDescription = repository.getMothDescription(moth.SpeciesFormalName)
                view.findViewById<TextView>(R.id.encyclopedia_entry_common_name).text =
                    moth.SpeciesVulgarName
                view.findViewById<TextView>(R.id.encyclopedia_entry_formal_view).text =
                    moth.SpeciesFormalName
                view.findViewById<TextView>(R.id.encyclopedia_entry_description).text =
                    mothDescription
                val rInt: Int? = imgMap[moth?.ReferencePhotographFileString?.removePrefix("/home/nmq-hyt/AndroidStudioProjects/Heterocera/app/src/main/assets/")?.removeSuffix(".jpeg")]
                view.findViewById<ImageView>(R.id.encyclopedia_entry_image)
                    .setImageResource(rInt!!)
            }
        }
    }
    companion object {

    }
}