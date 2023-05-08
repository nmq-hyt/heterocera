package com.heterocera.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.heterocera.R
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide


class HeteroceraTutorialFragment : Fragment() {
    private lateinit var tutorialAdapter: TutorialAdapter
    private lateinit var viewPager: ViewPager2

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_heterocera_tutorial, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tutorialAdapter = TutorialAdapter(this)
        viewPager = view.findViewById(R.id.pager)
        viewPager.adapter = tutorialAdapter

    }
}

class TutorialAdapter(fragment: Fragment): FragmentStateAdapter(fragment) {
    private val NUMBER_PAGES = 4
    override fun getItemCount(): Int {
        return NUMBER_PAGES
    }

    override fun createFragment(position: Int): Fragment {
        return HeteroceraTutorialSlideFragment(position)
    }
}

class HeteroceraTutorialSlideFragment(private val position: Int) : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.heterocera_tutorial_object, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val textViewOne = view.findViewById<TextView>(R.id.heterocera_tutorial_text_view_one)
        val textViewTwo = view.findViewById<TextView>(R.id.heterocera_tutorial_text_view_two)
        val imageViewOne = view.findViewById<ImageView>(R.id.heterocera_tutorial_image_view_one)
        val imageViewTwo= view.findViewById<ImageView>(R.id.heterocera_tutorial_image_view_two)

        if (position == 0) {
            textViewOne.text = resources.getString(R.string.instruction_welcome)
            Glide.with(view).load(android.R.drawable.ic_menu_help).into(imageViewTwo)
            textViewTwo.text= resources.getString(R.string.help_icon_instruction)
        }

        if (position == 1) {
            textViewOne.text = resources.getString(R.string.camera_help)
            Glide.with(view).load(android.R.drawable.ic_menu_camera).into(imageViewOne)
            textViewTwo.text= resources.getString(R.string.camera_capture_help)
        }

        if (position == 2) {
            textViewOne.text = resources.getString(R.string.observations_help)
            Glide.with(view).load(android.R.drawable.ic_menu_agenda).into(imageViewOne)
            textViewTwo.text = resources.getString(R.string.encyclopedia_help)
            Glide.with(view).load(android.R.drawable.ic_menu_search).into(imageViewTwo)
        }

        if (position == 3) {
            textViewOne.text = resources.getString(R.string.map_help)
            Glide.with(view).load(android.R.drawable.ic_menu_mapmode).into(imageViewOne)
            textViewTwo.text = resources.getString(R.string.thank_you)
        }






    }


}