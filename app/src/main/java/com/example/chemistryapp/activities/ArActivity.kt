package com.example.chemistryapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isGone
import com.example.chemistryapp.R
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import io.github.sceneview.ar.ArSceneView
import io.github.sceneview.ar.node.ArModelNode

class ArActivity : AppCompatActivity() {

    lateinit var sceneView: ArSceneView
    lateinit var placeButton: ExtendedFloatingActionButton
    lateinit var modelNode: ArModelNode

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ar)

        sceneView = findViewById(R.id.sceneview)
        placeButton = findViewById(R.id.place)

        placeButton.setOnClickListener {
            placeModel()
        }

        modelNode = ArModelNode().apply {
            loadModelGlbAsync(
                glbFileLocation = "threedmodels/bugatti.glb"
            ){
                sceneView.planeRenderer.isVisible = true
            }

            onAnchorChanged = {
                placeButton.isGone
            }
        }

        sceneView.addChild(modelNode)
    }

    private fun placeModel(){
        modelNode.anchor()
        sceneView.planeRenderer.isVisible = false
    }
}