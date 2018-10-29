package br.com.pedroso.valdeci.dangerouspermission

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks,
        EasyPermissions.RationaleCallbacks {
    companion object {
        const val PHONE_STATE_AND_LOCATION_REQUEST_CODE = 2268
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buildRequestPermission()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            showDialog()
        } else  {
            buildRequestPermission()
        }
    }

    private fun showDialog() {
        AppSettingsDialog
                .Builder(this)
                .setTitle(getString(R.string.prompt_permissions_title_needed))
                .setRationale(getString(R.string.rationale_some_permanently_denied))
                .setRequestCode(PHONE_STATE_AND_LOCATION_REQUEST_CODE)
                .build()
                .show()
    }

    private fun buildRequestPermission() {
        val permissions = mutableListOf<String>()
        val message: String
        if (!EasyPermissions.hasPermissions(this,
                        Manifest.permission.READ_PHONE_STATE) &&
                !EasyPermissions.hasPermissions(this,
                        Manifest.permission.ACCESS_FINE_LOCATION) &&
                !EasyPermissions.hasPermissions(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)) {
            permissions.add(Manifest.permission.READ_PHONE_STATE)
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)

            message = getString(R.string.rationale_message_permissions,
                    getString(R.string.app_name),
                    getString(R.string.prompt_permission_all))

        } else if (!EasyPermissions.hasPermissions(this,
                        Manifest.permission.READ_PHONE_STATE)) {
            permissions.add(Manifest.permission.READ_PHONE_STATE)

            message = getString(R.string.rationale_message_permissions,
                    getString(R.string.app_name),
                    getString(R.string.prompt_permission_phone_state))
        } else {
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)

            message = getString(R.string.rationale_message_permissions,
                    getString(R.string.app_name),
                    getString(R.string.prompt_permission_location))
        }

        if (!permissions.isEmpty()) {
            when {
                permissions.size == 1 -> {
                    EasyPermissions.requestPermissions(
                            this,
                            message,
                            PHONE_STATE_AND_LOCATION_REQUEST_CODE,
                            Manifest.permission.READ_PHONE_STATE)
                }
                permissions.size == 2 -> {
                    EasyPermissions.requestPermissions(
                            this,
                            message,
                            PHONE_STATE_AND_LOCATION_REQUEST_CODE,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION)
                }
                else -> {
                    EasyPermissions.requestPermissions(
                            this,
                            message,
                            PHONE_STATE_AND_LOCATION_REQUEST_CODE,
                            Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION)
                }
            }
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION)
                && EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                && EasyPermissions.hasPermissions(this, Manifest.permission.READ_PHONE_STATE)) {

            Toast.makeText(this, "All Permission granted!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if ((requestCode == PHONE_STATE_AND_LOCATION_REQUEST_CODE) &&
                (!EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        || !EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        || !EasyPermissions.hasPermissions(this, Manifest.permission.READ_PHONE_STATE))) {
            buildRequestPermission()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Enviando a resposta do usuário para a API EasyPermissions.
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this)
    }
    override fun onRationaleDenied(requestCode: Int) {
        showDialog()
    }

    override fun onRationaleAccepted(requestCode: Int) {}
}
