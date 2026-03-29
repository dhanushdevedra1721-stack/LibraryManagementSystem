data class BookEntry(
    val bookCode: String,
    var bookName: String,
    var writerName: String,
    var totalCopies: Int,
    var copiesLeft: Int
)

data class Member(
    val memberCode: String,
    var fullName: String,
    var activeLoans: Int = 0
)

data class IssuedBook(
    val issueId: Int,
    val bookCode: String,
    val memberCode: String,
    val issuedOnDay: Int,
    var returned: Boolean = false
)

class LibrarySystem {
    private val catalog = mutableListOf<BookEntry>()
    private val memberList = mutableListOf<Member>()
    private val issueLog = mutableListOf<IssuedBook>()
    private var day = 1
    private var issueCounter = 1
    private val maxLoanDays = 7
    private val finePerDay = 10

    fun currentDay() = day

    fun nextDay() {
        day++
        println("\n=== Day advanced. Current Day: $day ===")
    }

    // ── Book Operations ──────────────────────────────────────

    fun addBook(code: String, name: String, writer: String, copies: Int) {
        // FIX 3: Validate copies count
        if (copies <= 0) {
            println("Error: Number of copies must be greater than 0.")
            return
        }
        val existing = catalog.find { it.bookCode == code }
        if (existing != null) {
            existing.totalCopies += copies
            existing.copiesLeft += copies
            println("Updated: Added $copies more copies of '${existing.bookName}'. Total: ${existing.totalCopies}")
            return
        }
        catalog.add(BookEntry(code, name, writer, copies, copies))
        println("Added: '$name' by $writer ($copies copies)")
    }

    fun removeBook(code: String) {
        val book = catalog.find { it.bookCode == code }
        when {
            book == null -> println("Error: Book code '$code' not found.")
            book.copiesLeft < book.totalCopies -> println("Error: ${book.totalCopies - book.copiesLeft} copy/copies of '${book.bookName}' still on loan.")
            else -> {
                catalog.remove(book)
                println("Removed: '${book.bookName}' from catalog.")
            }
        }
    }

    fun searchBook(keyword: String) {
        val results = catalog.filter {
            it.bookName.contains(keyword, ignoreCase = true) ||
            it.writerName.contains(keyword, ignoreCase = true) ||
            it.bookCode.equals(keyword, ignoreCase = true)
        }
        if (results.isEmpty()) {
            println("No books found matching '$keyword'.")
        } else {
            println("\n--- Search Results for '$keyword' ---")
            results.forEach { printBook(it) }
        }
    }

    fun listBooks() {
        println("\n========== Book Catalog ==========")
        if (catalog.isEmpty()) {
            println("  No books available.")
        } else {
            catalog.forEach { printBook(it) }
        }
        println("==================================")
    }

    private fun printBook(b: BookEntry) {
        val status = if (b.copiesLeft > 0) "Available: ${b.copiesLeft}/${b.totalCopies}" else "OUT OF STOCK"
        println("  [${b.bookCode}] '${b.bookName}' by ${b.writerName} | $status")
    }

    // ── Member Operations ────────────────────────────────────

    fun addMember(code: String, name: String) {
        if (memberList.any { it.memberCode == code }) {
            println("Error: Member code '$code' already exists.")
            return
        }
        memberList.add(Member(code, name))
        println("Registered member: $name (ID: $code)")
    }

    fun removeMember(code: String) {
        val member = memberList.find { it.memberCode == code }
        when {
            member == null -> println("Error: Member '$code' not found.")
            member.activeLoans > 0 -> println("Error: ${member.fullName} still has ${member.activeLoans} book(s) to return.")
            else -> {
                memberList.remove(member)
                println("Removed member: ${member.fullName}")
            }
        }
    }

    fun listMembers() {
        println("\n========== Members ==========")
        if (memberList.isEmpty()) {
            println("  No members registered.")
        } else {
            memberList.forEach {
                println("  [${it.memberCode}] ${it.fullName} | Books on hand: ${it.activeLoans}")
            }
        }
        println("=============================")
    }

    // ── Loan Operations ──────────────────────────────────────

    fun issueBook(bookCode: String, memberCode: String) {
        val book = catalog.find { it.bookCode == bookCode }
        val member = memberList.find { it.memberCode == memberCode }

        // FIX 2: Check if member already has this book on loan
        val alreadyIssued = issueLog.any {
            it.bookCode == bookCode && it.memberCode == memberCode && !it.returned
        }

        when {
            book == null -> println("Error: Book '$bookCode' not found.")
            member == null -> println("Error: Member '$memberCode' not found.")
            book.copiesLeft == 0 -> println("Error: No copies of '${book.bookName}' available right now.")
            member.activeLoans >= 3 -> println("Error: ${member.fullName} has reached the loan limit (3 books).")
            alreadyIssued -> println("Error: ${member.fullName} already has '${book.bookName}' on loan.") // FIX 2
            else -> {
                book.copiesLeft--
                member.activeLoans++
                issueLog.add(IssuedBook(issueCounter++, bookCode, memberCode, day))
                println("Issued: '${book.bookName}' → ${member.fullName} (Day $day). Due by Day ${day + maxLoanDays}.")
            }
        }
    }

    fun returnBook(bookCode: String, memberCode: String) {
        val loan = issueLog.find {
            it.bookCode == bookCode && it.memberCode == memberCode && !it.returned
        }

        if (loan == null) {
            println("Error: No active loan found for Book '$bookCode' and Member '$memberCode'.")
            return
        }

        loan.returned = true
        val daysHeld = day - loan.issuedOnDay
        val overdue = daysHeld - maxLoanDays

        val book = catalog.find { it.bookCode == bookCode }
        val member = memberList.find { it.memberCode == memberCode }

        // FIX 1: Safe, direct increment instead of unsafe nullable assignment
        if (book != null) {
            book.copiesLeft++
        } else {
            println("Warning: Book '$bookCode' not found in catalog during return.")
        }

        if (member != null) {
            member.activeLoans--
        } else {
            println("Warning: Member '$memberCode' not found during return.")
        }

        println("Returned: '${book?.bookName}' by ${member?.fullName} on Day $day.")
        if (overdue > 0) {
            println("  ⚠ Overdue by $overdue day(s). Fine: Rs. ${overdue * finePerDay}")
        } else {
            println("  ✓ On time! ${-overdue} day(s) remaining from due date.")
        }
    }

    fun viewActiveLoans() {
        println("\n========== Active Loans ==========")
        val active = issueLog.filter { !it.returned }
        if (active.isEmpty()) {
            println("  No books currently on loan.")
        } else {
            active.forEach { loan ->
                val bookName = catalog.find { it.bookCode == loan.bookCode }?.bookName ?: loan.bookCode
                val memberName = memberList.find { it.memberCode == loan.memberCode }?.fullName ?: loan.memberCode
                val dueDay = loan.issuedOnDay + maxLoanDays
                val status = if (day > dueDay) "OVERDUE by ${day - dueDay} day(s)" else "Due Day $dueDay"
                println("  Issue#${loan.issueId} | '$bookName' → $memberName | $status")
            }
        }
        println("==================================")
    }
}

// FIX 4: Helper to read non-blank input with a prompt
fun readNonBlank(prompt: String): String? {
    print(prompt)
    val input = readln().trim()
    if (input.isBlank()) {
        println("Error: Input cannot be empty.")
        return null
    }
    return input
}

fun main() {
    val lib = LibrarySystem()
    var running = true

    println("╔══════════════════════════════════╗")
    println("║   Library Management System      ║")
    println("╚══════════════════════════════════╝")

    while (running) {
        println("\n--- Menu [Day ${lib.currentDay()}] ---")
        println(" 1. List All Books")
        println(" 2. Search Book")
        println(" 3. Add Book")
        println(" 4. Remove Book")
        println(" 5. List Members")
        println(" 6. Add Member")
        println(" 7. Remove Member")
        println(" 8. Issue Book")
        println(" 9. Return Book")
        println("10. View Active Loans")
        println("11. Next Day")
        println("12. Exit")
        print("Choice: ")

        when (readln().trim()) {
            "1" -> lib.listBooks()
            "2" -> {
                val keyword = readNonBlank("Enter title, author, or book code to search: ") ?: continue
                lib.searchBook(keyword)
            }
            "3" -> {
                val code = readNonBlank("Book Code: ") ?: continue
                val title = readNonBlank("Title: ") ?: continue
                val author = readNonBlank("Author: ") ?: continue
                print("Copies: ")
                // FIX 3: Validate copies input properly
                val copies = readln().trim().toIntOrNull()
                if (copies == null || copies <= 0) {
                    println("Error: Please enter a valid number of copies (greater than 0).")
                    continue
                }
                lib.addBook(code, title, author, copies)
            }
            "4" -> {
                lib.listBooks()
                val code = readNonBlank("Enter Book Code to remove: ") ?: continue
                lib.removeBook(code)
            }
            "5" -> lib.listMembers()
            "6" -> {
                val code = readNonBlank("Member Code: ") ?: continue
                val name = readNonBlank("Full Name: ") ?: continue
                lib.addMember(code, name)
            }
            "7" -> {
                lib.listMembers()
                val code = readNonBlank("Enter Member Code to remove: ") ?: continue
                lib.removeMember(code)
            }
            "8" -> {
                lib.listBooks(); lib.listMembers()
                val bc = readNonBlank("Book Code: ") ?: continue
                val mc = readNonBlank("Member Code: ") ?: continue
                lib.issueBook(bc, mc)
            }
            "9" -> {
                lib.viewActiveLoans()
                val bc = readNonBlank("Book Code: ") ?: continue
                val mc = readNonBlank("Member Code: ") ?: continue
                lib.returnBook(bc, mc)
            }
            "10" -> lib.viewActiveLoans()
            "11" -> lib.nextDay()
            "12" -> { running = false; println("Goodbye!") }
            else -> println("Invalid choice. Please enter 1–12.")
        }
    }
}
