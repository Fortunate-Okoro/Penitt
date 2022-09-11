package fortunate.signature.penit

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navOptions
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import fortunate.signature.penit.data.viewmodel.BaseNoteVM
import fortunate.signature.penit.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var configuration: AppBarConfiguration

    private val model: BaseNoteVM by viewModels()

    override fun onSupportNavigateUp(): Boolean = navController.navigateUp(configuration)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        setupNavigation()
        setupSearch()
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navNoteContainer) as NavHostFragment
        navController = navHostFragment.navController
        configuration = AppBarConfiguration(binding.navigationView.menu, binding.drawerLayout)
        setupActionBarWithNavController(navController, configuration)

        var fragmentIdToLoad: Int? = null
        binding.navigationView.setNavigationItemSelectedListener { item ->
            fragmentIdToLoad = item.itemId
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            return@setNavigationItemSelectedListener true
        }

        binding.drawerLayout.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {

            override fun onDrawerClosed(drawerView: View) {
                if (fragmentIdToLoad != null && navController.currentDestination?.id != fragmentIdToLoad) {
                    val options = navOptions {
                        launchSingleTop = true
                        anim {
                            exit = androidx.navigation.ui.R.anim.nav_default_enter_anim
                            enter = androidx.navigation.ui.R.anim.nav_default_enter_anim
                            popExit = androidx.navigation.ui.R.anim.nav_default_pop_exit_anim
                            popEnter = androidx.navigation.ui.R.anim.nav_default_pop_enter_anim
                        }
                        popUpTo(navController.graph.startDestinationId) { inclusive = false }
                    }
                    navController.navigate(requireNotNull(fragmentIdToLoad), null, options)
                }
            }
        })

        navController.addOnDestinationChangedListener { _, destination, _ ->
            fragmentIdToLoad = destination.id
            binding.navigationView.setCheckedItem(destination.id)
            handleDestinationChange(destination)
        }
    }

    private fun handleDestinationChange(destination: NavDestination) {
        when (destination.id) {
            R.id.notes -> {
                binding.btnCreateNew.show()
                binding.btnCreateNew.setImageResource(R.drawable.ic_edit)
            }
            R.id.notesLabels -> {
                binding.btnCreateNew.show()
                binding.btnCreateNew.setImageResource(R.drawable.ic_add)
            }
            R.id.tasks -> {
                binding.btnCreateNew.show()
                binding.btnCreateNew.setImageResource(R.drawable.ic_add_task)
            }
            else -> binding.btnCreateNew.hide()
        }
        binding.enterSearchKeyword.isVisible = (destination.id == R.id.search)
        binding.enterSearchKeyword.isVisible = (destination.id == R.id.searchTask)
//        when (destination.id) {
//            R.id.search -> {
//                binding.enterSearchKeyword.isVisible
//            }
//            R.id.searchTask -> {
//                binding.enterSearchKeyword.isVisible
//            }
//        }
    }

    private fun setupSearch() {
        binding.enterSearchKeyword.setText(model.keyword)
        binding.enterSearchKeyword.doAfterTextChanged { text ->
            model.keyword = text?.trim()?.toString() ?: String()
        }
    }
}
