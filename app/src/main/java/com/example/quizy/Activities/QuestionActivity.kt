package com.example.quizy.Activities

import android.animation.Animator
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quizy.R
import com.example.quizy.databinding.ActivityQuestionBinding
import kotlinx.coroutines.delay

data class QuestionModel(
    val question: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val correctAnswer: String

)

class QuestionActivity : AppCompatActivity() {
    private var count = 0
    private var position = 0
    private var score = 0
    private var isWaitingForNext = false
    private var timer: CountDownTimer? = null
    private val list = mutableListOf<QuestionModel>()
    private lateinit var binding: ActivityQuestionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuestionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        resetTimer()
        val intent: Intent = intent
        when (intent.getStringExtra("set")) {
            "Set 1" -> setOne()
            "Set 2" -> setTwo()
            "Set 3" -> setThree()
            "Set 4" -> setFour()
            "Set 5" -> setFive()
            "Set 6" -> setSix()
            "Set 7" -> setSeven()
            "Set 8" -> setEight()
            "Set 9" -> setNine()
            "Set 10" -> setTen()

            // Add cases for other sets here
            else -> Toast.makeText(
                applicationContext,
                "Encountered an Error While Opening Questions. Please Contact the Developer",
                Toast.LENGTH_SHORT
            ).show()
        }

        for (i in 0..3) {
            binding.optionContainer.getChildAt(i).setOnClickListener {
                checkAnswer(it as Button)
            }
        }

        playAnimation(binding.question, 0, list[position].question)

        binding.btnNext.setOnClickListener {
            position++
            if (position == list.size) {
                val intent = Intent(this@QuestionActivity, ScoreActivity::class.java)
                intent.putExtra("score", score)
                intent.putExtra("total", list.size)
                startActivity(intent)
                finish()
            } else {
                count = 0
                playAnimation(binding.question, 0, list[position].question)
                enableOption(true) // Re-enable option buttons for the next question
                isWaitingForNext = true // Set the flag to indicate waiting for the next question
                updateNextButtonState() // Update the state of the "Next" button
            }
        }
    }

    private fun resetTimer() {
        timer?.cancel() // Cancel the previous timer if it exists

        timer = object : CountDownTimer(31000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.timer.text = (millisUntilFinished / 1000).toString()
            }

            override fun onFinish() {
                val dialog = Dialog(this@QuestionActivity)
                dialog.window?.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND)
                dialog.setCancelable(false)
                dialog.setContentView(R.layout.timeout_dialog)
                dialog.findViewById<View>(R.id.tryAgain).setOnClickListener {
                    val intent = Intent(this@QuestionActivity, SetsActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                dialog.show()
            }
        }

        timer?.start() // Start the new timer
    }


    private fun updateNextButtonState() {
        if (isWaitingForNext) {
            binding.btnNext.isEnabled = false // Disable the "Next" button
            binding.btnNext.alpha = 0.7F // Reduce the opacity of the "Next" button
            binding.btnNext.setBackgroundResource(R.drawable.btn_next) // Change the background color of the "Next" button
        } else {
            binding.btnNext.isEnabled = true // Enable the "Next" button
            binding.btnNext.alpha = 1F // Set the opacity of the "Next" button to normal
            binding.btnNext.setBackgroundResource(R.drawable.btn_disabled) // Set the background color of the "Next" button to normal
        }
    }




    private fun playAnimation(view: View, i: Int, data: String) {
        view.animate()
            .alpha(i.toFloat())
            .scaleX(i.toFloat())
            .scaleY(i.toFloat())
            .setDuration(250)
            .setStartDelay(1)
            .setInterpolator(DecelerateInterpolator())
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                    if (i == 0 && count < 4) {
                        playAnimation(binding.question, 0, list[position].question)
                        playAnimation(binding.option1, 0, list[position].optionA)
                        playAnimation(binding.option2, 0, list[position].optionB)
                        playAnimation(binding.option3, 0, list[position].optionC)
                        playAnimation(binding.option4, 0, list[position].optionD)
                        count++
                    }
                }

                @SuppressLint("SetTextI18n")
                override fun onAnimationEnd(animation: Animator) {
                    if (i == 0) {
                        try {
                            (view as? TextView)?.text = data
                            binding.totalQuestions.text = "${position + 1}/${list.size}"
                            setOptionButtonText()
                        } catch (e: Exception) {
                            (view as? Button)?.text = data
                        }
                        view.tag = data
                        playAnimation(view, 1, data)
                    }
                }

                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            })
    }


    private fun setOptionButtonText() {
        val options = listOf(
            list[position].optionA,
            list[position].optionB,
            list[position].optionC,
            list[position].optionD
        )
        for (i in 0 until binding.optionContainer.childCount) {
            val optionButton = binding.optionContainer.getChildAt(i) as? Button
            optionButton?.text = options[i]
        }
    }
    private fun checkAnswer(selectedOption: Button) {
        if(timer!=null){
            timer?.cancel()
        }

        binding.btnNext.isEnabled = false

        binding.btnNext.alpha = 0.7F
        enableOption(false) // Disable all option buttons to prevent further selection

        val selectedAnswer = selectedOption.text.toString()
        val correctAnswer = list[position].correctAnswer

        if (selectedAnswer == correctAnswer) {
            score++
            selectedOption.setBackgroundResource(R.drawable.right_ans)
        } else {
            selectedOption.setBackgroundResource(R.drawable.wrong_btn)
            val correctOption = getCorrectOptionButton(correctAnswer)
            correctOption.setBackgroundResource(R.drawable.right_ans)
        }
        isWaitingForNext = true // Set the flag to indicate waiting for the next question

        updateNextButtonState()

        // Delay the next question loading to allow time for highlighting the correct answer
        binding.btnNext.postDelayed({
            position++
            if (position == list.size) {
                val intent = Intent(this@QuestionActivity, ScoreActivity::class.java)
                intent.putExtra("score", score)
                intent.putExtra("total", list.size)
                startActivity(intent)
                finish()
            } else {
                count = 0
                playAnimation(binding.question, 0, list[position].question)
                enableOption(true) // Re-enable option buttons for the next question
                isWaitingForNext = false // Set the flag to indicate waiting for the next question is over
                updateNextButtonState()
            }
            resetTimer()
        }, 1000)
    }

    private fun enableOption(enable: Boolean) {
        for (i in 0 until binding.optionContainer.childCount) {
            val optionButton = binding.optionContainer.getChildAt(i) as Button
            optionButton.isEnabled = enable
            optionButton.setBackgroundResource(R.drawable.btn_opt)
        }
    }

    private fun getCorrectOptionButton(correctAnswer: String): Button {
        for (i in 0 until binding.optionContainer.childCount) {
            val optionButton = binding.optionContainer.getChildAt(i) as Button
            if (optionButton.text.toString() == correctAnswer) {
                return optionButton
            }
        }
        throw IllegalStateException("Correct option button not found")
    }




    private fun setOne() {

        list.add(
            QuestionModel(
                "Who painted the Mona Lisa?",
                "A. Leonardo da Vinci",
                "B. Pablo Picasso",
                "C. Vincent van Gogh",
                "D. Michelangelo",
                "A. Leonardo da Vinci"
            )
        )
        list.add(
            QuestionModel(
                "Which city is famous for its Taj Mahal?",
                "A. Delhi",
                "B. Mumbai",
                "C. Agra",
                "D. Jaipur",
                "C. Agra"
            )
        )
        list.add(
            QuestionModel(
                "Which civilization built the Machu Picchu citadel in Peru?",
                "A. Mayans",
                "B. Aztecs",
                "C. Incas",
                "D. Inuit",
                "C. Incas"
            )
        )
        list.add(
            QuestionModel(
                "Who is known as the 'Father of the Renaissance'?",
                "A. Galileo Galilei",
                "B. Nicolaus Copernicus",
                "C. Leonardo da Vinci",
                "D. Michelangelo",
                "C. Leonardo da Vinci"
            )
        )
        list.add(
            QuestionModel(
                "In what year was the United States Declaration of Independence adopted?",
                "A. 1776",
                "B. 1789",
                "C. 1812",
                "D. 1865",
                "A. 1776"
            )
        )
        list.add(
            QuestionModel(
                "Which ancient civilization built the Great Wall of China?",
                "A. Mongols",
                "B. Qin Dynasty",
                "C. Han Dynasty",
                "D. Tang Dynasty",
                "B. Qin Dynasty"
            )
        )
        list.add(
            QuestionModel(
                "Who wrote the famous novel 'Pride and Prejudice'?",
                "A. Charlotte Bronte",
                "B. Jane Austen",
                "C. Emily Dickinson",
                "D. Virginia Woolf",
                "B. Jane Austen"
            )
        )
        list.add(
            QuestionModel(
                "Which city is famous for its historical Acropolis?",
                "A. Rome",
                "B. Athens",
                "C. Cairo",
                "D. Paris",
                "B. Athens"
            )
        )
        list.add(
            QuestionModel(
                "Who was the first President of the United States?",
                "A. George Washington",
                "B. Thomas Jefferson",
                "C. John Adams",
                "D. Abraham Lincoln",
                "A. George Washington"
            )
        )
        list.add(
            QuestionModel(
                "Which ancient wonder was located in Egypt?",
                "A. The Colosseum",
                "B. The Great Wall",
                "C. The Parthenon",
                "D. The Pyramids of Giza",
                "D. The Pyramids of Giza"
            )
        )
    }

    private fun setTwo() {
        list.add(
            QuestionModel(
                "Who was the first President of the United States?",
                "A. George Washington",
                "B. Thomas Jefferson",
                "C. John Adams",
                "D. Abraham Lincoln",
                "A. George Washington"
            )
        )
        list.add(
            QuestionModel(
                "Which famous document begins with the words 'When in the Course of human events...'?",
                "A. The Declaration of Independence",
                "B. The Emancipation Proclamation",
                "C. The Bill of Rights",
                "D. The Gettysburg Address",
                "A. The Declaration of Independence"
            )
        )
        list.add(
            QuestionModel(
                "Who painted the ceiling of the Sistine Chapel?",
                "A. Leonardo da Vinci",
                "B. Michelangelo",
                "C. Pablo Picasso",
                "D. Vincent van Gogh",
                "B. Michelangelo"
            )
        )
        list.add(
            QuestionModel(
                "Which city is known as the 'Eternal City'?",
                "A. Paris",
                "B. Rome",
                "C. Athens",
                "D. Cairo",
                "B. Rome"
            )
        )
        list.add(
            QuestionModel(
                "In what year did World War II end?",
                "A. 1943",
                "B. 1945",
                "C. 1947",
                "D. 1950",
                "B. 1945"
            )
        )
        list.add(
            QuestionModel(
                "Who wrote the famous play 'Romeo and Juliet'?",
                "A. William Shakespeare",
                "B. Arthur Miller",
                "C. Tennessee Williams",
                "D. Oscar Wilde",
                "A. William Shakespeare"
            )
        )
        list.add(
            QuestionModel(
                "Who was the first man to walk on the moon?",
                "A. Neil Armstrong",
                "B. Buzz Aldrin",
                "C. Yuri Gagarin",
                "D. John F. Kennedy",
                "A. Neil Armstrong"
            )
        )
        list.add(
            QuestionModel(
                "Who painted the famous artwork 'The Starry Night'?",
                "A. Vincent van Gogh",
                "B. Pablo Picasso",
                "C. Leonardo da Vinci",
                "D. Salvador Dali",
                "A. Vincent van Gogh"
            )
        )
        list.add(
            QuestionModel(
                "Which ancient wonder was located in Egypt?",
                "A. The Colosseum",
                "B. The Great Wall",
                "C. The Parthenon",
                "D. The Pyramids of Giza",
                "D. The Pyramids of Giza"
            )
        )
        list.add(
            QuestionModel(
                "Who was the Prime Minister of the United Kingdom during World War II?",
                "A. Winston Churchill",
                "B. Margaret Thatcher",
                "C. Tony Blair",
                "D. David Cameron",
                "A. Winston Churchill"
            )
        )
    }

    private fun setThree() {
        list.add(
            QuestionModel(
                "Who painted the Mona Lisa?",
                "A. Leonardo da Vinci",
                "B. Pablo Picasso",
                "C. Vincent van Gogh",
                "D. Claude Monet",
                "A. Leonardo da Vinci"
            )
        )
        list.add(
            QuestionModel(
                "Which war was fought between the North and South regions of the United States?",
                "A. American Revolution",
                "B. World War I",
                "C. Civil War",
                "D. Cold War",
                "C. Civil War"
            )
        )
        list.add(
            QuestionModel(
                "Which ancient civilization built the Great Wall of China?",
                "A. Romans",
                "B. Greeks",
                "C. Egyptians",
                "D. Chinese",
                "D. Chinese"
            )
        )
        list.add(
            QuestionModel(
                "Who was the first female Prime Minister of India?",
                "A. Indira Gandhi",
                "B. Margaret Thatcher",
                "C. Golda Meir",
                "D. Benazir Bhutto",
                "A. Indira Gandhi"
            )
        )
        list.add(
            QuestionModel(
                "Which famous battle marked the end of Napoleon Bonaparte's rule?",
                "A. Battle of Waterloo",
                "B. Battle of Gettysburg",
                "C. Battle of Stalingrad",
                "D. Battle of Hastings",
                "A. Battle of Waterloo"
            )
        )
        list.add(
            QuestionModel(
                "Which country was ruled by Adolf Hitler during World War II?",
                "A. Germany",
                "B. United States",
                "C. United Kingdom",
                "D. Japan",
                "A. Germany"
            )
        )
        list.add(
            QuestionModel(
                "Who wrote the Communist Manifesto?",
                "A. Karl Marx and Friedrich Engels",
                "B. Adam Smith",
                "C. John Locke",
                "D. Karl Popper",
                "A. Karl Marx and Friedrich Engels"
            )
        )
        list.add(
            QuestionModel(
                "Which country colonized most of Southeast Asia in the 19th and 20th centuries?",
                "A. Spain",
                "B. France",
                "C. England",
                "D. Portugal",
                "B. France"
            )
        )
        list.add(
            QuestionModel(
                "Which famous physicist developed the theory of general relativity?",
                "A. Isaac Newton",
                "B. Albert Einstein",
                "C. Stephen Hawking",
                "D. Niels Bohr",
                "B. Albert Einstein"
            )
        )
        list.add(
            QuestionModel(
                "Who was the first African American President of the United States?",
                "A. Barack Obama",
                "B. Martin Luther King Jr.",
                "C. Malcolm X",
                "D. Nelson Mandela",
                "A. Barack Obama"
            )
        )
    }

    private fun setFour() {
        list.add(
            QuestionModel(
                "Who invented the printing press?",
                "A. Johannes Gutenberg",
                "B. Alexander Graham Bell",
                "C. Thomas Edison",
                "D. Isaac Newton",
                "A. Johannes Gutenberg"
            )
        )
        list.add(
            QuestionModel(
                "Which ancient civilization built the city of Machu Picchu?",
                "A. Aztecs",
                "B. Incas",
                "C. Mayans",
                "D. Egyptians",
                "B. Incas"
            )
        )
        list.add(
            QuestionModel(
                "Who was the first European explorer to reach India by sea?",
                "A. Christopher Columbus",
                "B. Vasco da Gama",
                "C. Ferdinand Magellan",
                "D. James Cook",
                "B. Vasco da Gama"
            )
        )
        list.add(
            QuestionModel(
                "Which treaty ended World War I?",
                "A. Treaty of Versailles",
                "B. Treaty of Paris",
                "C. Treaty of Tordesillas",
                "D. Treaty of Ghent",
                "A. Treaty of Versailles"
            )
        )
        list.add(
            QuestionModel(
                "Who founded the Mongol Empire?",
                "A. Genghis Khan",
                "B. Alexander the Great",
                "C. Julius Caesar",
                "D. Attila the Hun",
                "A. Genghis Khan"
            )
        )
        list.add(
            QuestionModel(
                "Which city hosted the first modern Olympic Games in 1896?",
                "A. Athens",
                "B. Paris",
                "C. Rome",
                "D. London",
                "A. Athens"
            )
        )
        list.add(
            QuestionModel(
                "Who wrote the novel '1984'?",
                "A. George Orwell",
                "B. Aldous Huxley",
                "C. Ray Bradbury",
                "D. J.D. Salinger",
                "A. George Orwell"
            )
        )
        list.add(
            QuestionModel(
                "Which ancient civilization built the city of Petra?",
                "A. Egyptians",
                "B. Persians",
                "C. Greeks",
                "D. Nabateans",
                "D. Nabateans"
            )
        )
        list.add(
            QuestionModel(
                "Who was the first person to circumnavigate the globe?",
                "A. Ferdinand Magellan",
                "B. Christopher Columbus",
                "C. James Cook",
                "D. Marco Polo",
                "A. Ferdinand Magellan"
            )
        )
        list.add(
            QuestionModel(
                "Which event marked the beginning of the French Revolution?",
                "A. Storming of the Bastille",
                "B. Battle of Waterloo",
                "C. Boston Tea Party",
                "D. Declaration of Independence",
                "A. Storming of the Bastille"
            )
        )
    }

    private fun setFive() {
        list.add(
            QuestionModel(
                "Who is credited with the invention of the telephone?",
                "A. Alexander Graham Bell",
                "B. Thomas Edison",
                "C. Nikola Tesla",
                "D. Galileo Galilei",
                "A. Alexander Graham Bell"
            )
        )
        list.add(
            QuestionModel(
                "Who was the first Emperor of China?",
                "A. Qin Shi Huang",
                "B. Genghis Khan",
                "C. Sun Yat-sen",
                "D. Mao Zedong",
                "A. Qin Shi Huang"
            )
        )
        list.add(
            QuestionModel(
                "Which ancient civilization built the city of Troy?",
                "A. Greeks",
                "B. Egyptians",
                "C. Romans",
                "D. Persians",
                "A. Greeks"
            )
        )
        list.add(
            QuestionModel(
                "Who was the leader of the Soviet Union during World War II?",
                "A. Joseph Stalin",
                "B. Vladimir Lenin",
                "C. Mikhail Gorbachev",
                "D. Nikita Khrushchev",
                "A. Joseph Stalin"
            )
        )
        list.add(
            QuestionModel(
                "Which famous battle marked the end of the Roman Republic?",
                "A. Battle of Actium",
                "B. Battle of Marathon",
                "C. Battle of Thermopylae",
                "D. Battle of Hastings",
                "A. Battle of Actium"
            )
        )
        list.add(
            QuestionModel(
                "Who was the first Emperor of Rome?",
                "A. Julius Caesar",
                "B. Augustus",
                "C. Constantine",
                "D. Nero",
                "B. Augustus"
            )
        )
        list.add(
            QuestionModel(
                "Which country was ruled by Queen Elizabeth I?",
                "A. England",
                "B. France",
                "C. Spain",
                "D. Portugal",
                "A. England"
            )
        )
        list.add(
            QuestionModel(
                "Who wrote the famous book 'To Kill a Mockingbird'?",
                "A. Harper Lee",
                "B. J.R.R. Tolkien",
                "C. F. Scott Fitzgerald",
                "D. Mark Twain",
                "A. Harper Lee"
            )
        )
        list.add(
            QuestionModel(
                "Which ancient civilization built the city of Angkor Wat?",
                "A. Egyptians",
                "B. Aztecs",
                "C. Mayans",
                "D. Khmers",
                "D. Khmers"
            )
        )
        list.add(
            QuestionModel(
                "Who was the first President of the United States?",
                "A. George Washington",
                "B. Thomas Jefferson",
                "C. John Adams",
                "D. Abraham Lincoln",
                "A. George Washington"
            )
        )
    }

    private fun setSix() {
        list.add(
            QuestionModel(
                "Who was the first female Prime Minister of the United Kingdom?",
                "A. Margaret Thatcher",
                "B. Angela Merkel",
                "C. Indira Gandhi",
                "D. Theresa May",
                "A. Margaret Thatcher"
            )
        )
        list.add(
            QuestionModel(
                "Which ancient civilization built the city of Athens?",
                "A. Romans",
                "B. Greeks",
                "C. Egyptians",
                "D. Persians",
                "B. Greeks"
            )
        )
        list.add(
            QuestionModel(
                "Who is known as the 'Father of the Constitution'?",
                "A. George Washington",
                "B. Thomas Jefferson",
                "C. James Madison",
                "D. Alexander Hamilton",
                "C. James Madison"
            )
        )
        list.add(
            QuestionModel(
                "Which famous battle marked the end of the Napoleonic Wars?",
                "A. Battle of Waterloo",
                "B. Battle of Gettysburg",
                "C. Battle of Stalingrad",
                "D. Battle of Hastings",
                "A. Battle of Waterloo"
            )
        )
        list.add(
            QuestionModel(
                "Who was the first person to sail around the world?",
                "A. Ferdinand Magellan",
                "B. Christopher Columbus",
                "C. James Cook",
                "D. Marco Polo",
                "A. Ferdinand Magellan"
            )
        )
        list.add(
            QuestionModel(
                "Who discovered penicillin?",
                "A. Alexander Fleming",
                "B. Louis Pasteur",
                "C. Marie Curie",
                "D. Robert Koch",
                "A. Alexander Fleming"
            )
        )
        list.add(
            QuestionModel(
                "Which country was ruled by Queen Victoria?",
                "A. England",
                "B. France",
                "C. Spain",
                "D. Portugal",
                "A. England"
            )
        )
        list.add(
            QuestionModel(
                "Who wrote the famous novel 'Pride and Prejudice'?",
                "A. Jane Austen",
                "B. Charlotte Brontë",
                "C. Emily Dickinson",
                "D. Virginia Woolf",
                "A. Jane Austen"
            )
        )
        list.add(
            QuestionModel(
                "Which ancient civilization built the city of Rome?",
                "A. Greeks",
                "B. Egyptians",
                "C. Persians",
                "D. Romans",
                "D. Romans"
            )
        )
        list.add(
            QuestionModel(
                "Who was the leader of the Soviet Union during the Cuban Missile Crisis?",
                "A. Nikita Khrushchev",
                "B. Joseph Stalin",
                "C. Leonid Brezhnev",
                "D. Vladimir Putin",
                "A. Nikita Khrushchev"
            )
        )
    }

    private fun setSeven() {
        list.add(
            QuestionModel(
                "Who painted the famous artwork 'The Last Supper'?",
                "A. Leonardo da Vinci",
                "B. Pablo Picasso",
                "C. Vincent van Gogh",
                "D. Claude Monet",
                "A. Leonardo da Vinci"
            )
        )
        list.add(
            QuestionModel(
                "Which famous document begins with the words 'We the People'?",
                "A. The Declaration of Independence",
                "B. The Emancipation Proclamation",
                "C. The Bill of Rights",
                "D. The U.S. Constitution",
                "D. The U.S. Constitution"
            )
        )
        list.add(
            QuestionModel(
                "Which ancient civilization built the city of Pompeii?",
                "A. Greeks",
                "B. Egyptians",
                "C. Romans",
                "D. Persians",
                "C. Romans"
            )
        )
        list.add(
            QuestionModel(
                "Who was the first President of the United States of America?",
                "A. George Washington",
                "B. Thomas Jefferson",
                "C. John Adams",
                "D. Abraham Lincoln",
                "A. George Washington"
            )
        )
        list.add(
            QuestionModel(
                "Which country was ruled by Queen Elizabeth II?",
                "A. United Kingdom",
                "B. Canada",
                "C. Australia",
                "D. All of the above",
                "D. All of the above"
            )
        )
        list.add(
            QuestionModel(
                "Who wrote the famous play 'Hamlet'?",
                "A. William Shakespeare",
                "B. Arthur Miller",
                "C. Tennessee Williams",
                "D. Oscar Wilde",
                "A. William Shakespeare"
            )
        )
        list.add(
            QuestionModel(
                "Who discovered the theory of gravity?",
                "A. Isaac Newton",
                "B. Albert Einstein",
                "C. Galileo Galilei",
                "D. Charles Darwin",
                "A. Isaac Newton"
            )
        )
        list.add(
            QuestionModel(
                "Which city is known as the 'City of Light'?",
                "A. Paris",
                "B. Rome",
                "C. Athens",
                "D. Cairo",
                "A. Paris"
            )
        )
        list.add(
            QuestionModel(
                "In what year did the Berlin Wall fall?",
                "A. 1987",
                "B. 1989",
                "C. 1991",
                "D. 1993",
                "B. 1989"
            )
        )
        list.add(
            QuestionModel(
                "Who was the author of the book 'The Catcher in the Rye'?",
                "A. J.D. Salinger",
                "B. F. Scott Fitzgerald",
                "C. Ernest Hemingway",
                "D. Mark Twain",
                "A. J.D. Salinger"
            )
        )
    }

    private fun setEight() {
        list.add(
            QuestionModel(
                "Who painted the famous artwork 'Starry Night'?",
                "A. Vincent van Gogh",
                "B. Pablo Picasso",
                "C. Leonardo da Vinci",
                "D. Claude Monet",
                "A. Vincent van Gogh"
            )
        )
        list.add(
            QuestionModel(
                "Which ancient civilization built the city of Cairo?",
                "A. Greeks",
                "B. Egyptians",
                "C. Romans",
                "D. Persians",
                "B. Egyptians"
            )
        )
        list.add(
            QuestionModel(
                "Who was the first person to set foot on the moon?",
                "A. Neil Armstrong",
                "B. Buzz Aldrin",
                "C. Yuri Gagarin",
                "D. John F. Kennedy",
                "A. Neil Armstrong"
            )
        )
        list.add(
            QuestionModel(
                "Which country was ruled by Queen Cleopatra?",
                "A. Egypt",
                "B. Greece",
                "C. Persia",
                "D. Rome",
                "A. Egypt"
            )
        )
        list.add(
            QuestionModel(
                "Who wrote the famous novel 'Moby-Dick'?",
                "A. Herman Melville",
                "B. Mark Twain",
                "C. F. Scott Fitzgerald",
                "D. Emily Dickinson",
                "A. Herman Melville"
            )
        )
        list.add(
            QuestionModel(
                "Who is known as the 'Father of the American Revolution'?",
                "A. George Washington",
                "B. Thomas Jefferson",
                "C. John Adams",
                "D. Benjamin Franklin",
                "D. Benjamin Franklin"
            )
        )
        list.add(
            QuestionModel(
                "Which country was ruled by Emperor Napoleon Bonaparte?",
                "A. France",
                "B. England",
                "C. Spain",
                "D. Germany",
                "A. France"
            )
        )
        list.add(
            QuestionModel(
                "Who composed the famous symphony 'Symphony No. 9'?",
                "A. Ludwig van Beethoven",
                "B. Wolfgang Amadeus Mozart",
                "C. Johann Sebastian Bach",
                "D. Franz Schubert",
                "A. Ludwig van Beethoven"
            )
        )
        list.add(
            QuestionModel(
                "Which city is known as the 'Eternal City'?",
                "A. Rome",
                "B. Athens",
                "C. Paris",
                "D. Cairo",
                "A. Rome"
            )
        )
        list.add(
            QuestionModel(
                "Who was the leader of the Soviet Union during the Cold War?",
                "A. Nikita Khrushchev",
                "B. Joseph Stalin",
                "C. Mikhail Gorbachev",
                "D. Vladimir Putin",
                "A. Nikita Khrushchev"
            )
        )
    }

    private fun setNine() {
        list.add(
            QuestionModel(
                "Who painted the famous artwork 'The Mona Lisa'?",
                "A. Leonardo da Vinci",
                "B. Pablo Picasso",
                "C. Vincent van Gogh",
                "D. Claude Monet",
                "A. Leonardo da Vinci"
            )
        )
        list.add(
            QuestionModel(
                "Which ancient civilization built the city of Babylon?",
                "A. Greeks",
                "B. Egyptians",
                "C. Persians",
                "D. Babylonians",
                "D. Babylonians"
            )
        )
        list.add(
            QuestionModel(
                "Who wrote the famous novel 'The Great Gatsby'?",
                "A. F. Scott Fitzgerald",
                "B. Ernest Hemingway",
                "C. Harper Lee",
                "D. William Faulkner",
                "A. F. Scott Fitzgerald"
            )
        )
        list.add(
            QuestionModel(
                "Which country was ruled by Emperor Charlemagne?",
                "A. France",
                "B. England",
                "C. Spain",
                "D. Germany",
                "A. France"
            )
        )
        list.add(
            QuestionModel(
                "Who is known as the 'Father of the American Revolution'?",
                "A. George Washington",
                "B. Thomas Jefferson",
                "C. John Adams",
                "D. Benjamin Franklin",
                "D. Benjamin Franklin"
            )
        )
        list.add(
            QuestionModel(
                "Who is credited with the discovery of electricity?",
                "A. Benjamin Franklin",
                "B. Thomas Edison",
                "C. Nikola Tesla",
                "D. James Watt",
                "A. Benjamin Franklin"
            )
        )
        list.add(
            QuestionModel(
                "Which city is known as the 'City of Love'?",
                "A. Paris",
                "B. Rome",
                "C. Venice",
                "D. Florence",
                "A. Paris"
            )
        )
        list.add(
            QuestionModel(
                "Who was the leader of the Soviet Union during the Space Race?",
                "A. Nikita Khrushchev",
                "B. Joseph Stalin",
                "C. Mikhail Gorbachev",
                "D. Vladimir Putin",
                "A. Nikita Khrushchev"
            )
        )
        list.add(
            QuestionModel(
                "Who wrote the famous play 'Romeo and Juliet'?",
                "A. William Shakespeare",
                "B. Arthur Miller",
                "C. Tennessee Williams",
                "D. Oscar Wilde",
                "A. William Shakespeare"
            )
        )
        list.add(
            QuestionModel(
                "Which country was ruled by King Louis XIV?",
                "A. France",
                "B. England",
                "C. Spain",
                "D. Germany",
                "A. France"
            )
        )
    }

    private fun setTen() {
        list.add(
            QuestionModel(
                "Who painted the famous artwork 'The Scream'?",
                "A. Edvard Munch",
                "B. Pablo Picasso",
                "C. Vincent van Gogh",
                "D. Claude Monet",
                "A. Edvard Munch"
            )
        )
        list.add(
            QuestionModel(
                "Which ancient civilization built the city of Machu Picchu?",
                "A. Aztecs",
                "B. Mayans",
                "C. Incas",
                "D. Egyptians",
                "C. Incas"
            )
        )
        list.add(
            QuestionModel(
                "Who wrote the famous novel 'War and Peace'?",
                "A. Leo Tolstoy",
                "B. Fyodor Dostoevsky",
                "C. Anton Chekhov",
                "D. Vladimir Nabokov",
                "A. Leo Tolstoy"
            )
        )
        list.add(
            QuestionModel(
                "Which country was ruled by Emperor Hirohito?",
                "A. Japan",
                "B. China",
                "C. India",
                "D. South Korea",
                "A. Japan"
            )
        )
        list.add(
            QuestionModel(
                "Who is known as the 'Father of Modern Physics'?",
                "A. Albert Einstein",
                "B. Isaac Newton",
                "C. Niels Bohr",
                "D. Max Planck",
                "A. Albert Einstein"
            )
        )
        list.add(
            QuestionModel(
                "Who is credited with the invention of the telephone?",
                "A. Alexander Graham Bell",
                "B. Thomas Edison",
                "C. Nikola Tesla",
                "D. Guglielmo Marconi",
                "A. Alexander Graham Bell"
            )
        )
        list.add(
            QuestionModel(
                "Which city is known as the 'City of Dreams'?",
                "A. New York City",
                "B. Los Angeles",
                "C. Mumbai",
                "D. Sydney",
                "C. Mumbai"
            )
        )
        list.add(
            QuestionModel(
                "Who was the leader of the Soviet Union during World War II?",
                "A. Joseph Stalin",
                "B. Nikita Khrushchev",
                "C. Mikhail Gorbachev",
                "D. Vladimir Putin",
                "A. Joseph Stalin"
            )
        )
        list.add(
            QuestionModel(
                "Who wrote the famous play 'Macbeth'?",
                "A. William Shakespeare",
                "B. Arthur Miller",
                "C. Tennessee Williams",
                "D. Oscar Wilde",
                "A. William Shakespeare"
            )
        )
        list.add(
            QuestionModel(
                "Which country was ruled by Queen Isabella I?",
                "A. Spain",
                "B. England",
                "C. France",
                "D. Portugal",
                "A. Spain"
            )
        )
    }
}


