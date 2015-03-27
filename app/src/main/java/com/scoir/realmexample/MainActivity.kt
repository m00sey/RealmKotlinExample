package com.scoir.realmexample

import android.content.Context
import android.os.Bundle
import android.support.v7.app.ActionBarActivity
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import com.scoir.realmexample.realm.Person
import io.realm.Realm
import io.realm.RealmBaseAdapter
import io.realm.RealmResults
import kotlinx.android.synthetic.activity_main.listView
import kotlin.properties.Delegates

public class MainActivity : ActionBarActivity() {
    var realm: Realm by Delegates.notNull()
    val json = """
        [
            {
                "name": "George"
            },
            {
                "name": "Paul"
            },
            {
                "name": "Ringo"
            },
            {
                "name": "John"
            }
        ]
    """

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        realm = Realm.getInstance(this)
        populateRealm()
    }

    private fun populateRealm() {
        realm.beginTransaction()
        realm.createOrUpdateAllFromJson(javaClass<Person>(), json)
        realm.commitTransaction()
    }

    override fun onResume() {
        super.onResume()
        var people = realm.allObjects(javaClass<Person>())
        people.sort("name", RealmResults.SORT_ORDER_ASCENDING)

        var personAdapter = PersonAdapter(this, people, true)

        listView.setAdapter(personAdapter)
    }
}

class PersonAdapter(context: Context, realmResults: RealmResults<Person>?, automaticUpdate: Boolean) :
        RealmBaseAdapter<Person>(context: Context, realmResults: RealmResults<Person>?, automaticUpdate: Boolean) {

    val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return realmResults.size()
    }

    override fun getItem(position: Int): Person {
        return realmResults.get(position)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        if (convertView == null) {
            var view = layoutInflater.inflate(R.layout.person_item, null) as PersonItemView
            view.populate(getItem(position))
            return view
        } else {
            (convertView as PersonItemView).populate(getItem(position))
            return convertView
        }
    }
}

class PersonItemView : RelativeLayout {
    private var personName: TextView by Delegates.notNull()

    constructor (context: Context) : super(context) {
    }

    [suppress("UNUSED_PARAMETER")]
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    }

    [suppress("UNUSED_PARAMETER")]
    constructor(context: Context, attrs: AttributeSet, defaultAttrsId: Int) : super(context, attrs, defaultAttrsId) {
    }

    public fun populate(person: Person) {
        personName = this.findViewById(R.id.personName) as TextView
        personName.setText(person.getName())
    }
}
