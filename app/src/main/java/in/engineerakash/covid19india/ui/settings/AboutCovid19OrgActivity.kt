package `in`.engineerakash.covid19india.ui.settings

import `in`.engineerakash.covid19india.R
import `in`.engineerakash.covid19india.databinding.ActivityAboutCovid19OrgBinding
import `in`.engineerakash.covid19india.pojo.QAndA
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity

class AboutCovid19OrgActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAboutCovid19OrgBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutCovid19OrgBinding.inflate(layoutInflater)

        setContentView(binding.root)

        initComponent()
    }

    private fun initComponent() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        val qAndAAdapter = QAndAAdapter(getQAndAData())
        binding.qAndARv.adapter = qAndAAdapter
    }

    private fun getQAndAData(): ArrayList<QAndA> {
        val qAndAList = arrayListOf<QAndA>()

        val questionArray: Array<String> = resources.getStringArray(R.array.q_and_a_questions_list)
        val answerArray: Array<String> = resources.getStringArray(R.array.q_and_a_answers_list)

        for ((index, question) in questionArray.withIndex())
            qAndAList.add(QAndA(question, answerArray[index]))

        return qAndAList
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

}