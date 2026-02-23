package org.dbu.library.ui

import org.dbu.library.model.Book
import org.dbu.library.model.Patron
import org.dbu.library.repository.LibraryRepository
import org.dbu.library.service.BorrowResult
import org.dbu.library.service.LibraryService

fun handleMenuAction(
    choice: String,
    service: LibraryService,
    repository: LibraryRepository
): Boolean {

    return when (choice) {

        "1" -> {
            addBook(service)
            true
        }

        "2" -> {
            registerPatron(repository)
            true
        }

        "3" -> {
            borrowBook(service)
            true
        }

        "4" -> {
            returnBook(service)
            true
        }

        "5" -> {
            search(service)
            true
        }

        "6" -> {
            listAllBooks(repository)
            true
        }

        "7" -> {
            listAllPatrons(repository)
            true
        }

        "0" -> false

        else -> {
            println("Invalid option")
            true
        }
    }
}

private fun addBook(service: LibraryService) {
    println("\n--- Add New Book ---")
    print("Enter ISBN: ")
    val isbn = readln().trim()
    print("Enter Title: ")
    val title = readln().trim()
    print("Enter Author: ")
    val author = readln().trim()
    print("Enter Year: ")
    val yearInput = readln().trim()
    val year = yearInput.toIntOrNull() ?: 0

    if (isbn.isEmpty() || title.isEmpty()) {
        println("Error: ISBN and Title cannot be empty.")
        return
    }

    val book = Book(isbn, title, author, year)
    if (service.addBook(book)) {
        println("Success: '$title' added to library.")
    } else {
        println("Error: A book with ISBN $isbn already exists!")
    }
}

private fun registerPatron(repository: LibraryRepository) {
    println("\n--- Register Patron ---")
    print("Enter ID: ")
    val id = readln().trim()
    print("Enter Name: ")
    val name = readln().trim()

    if (id.isEmpty() || name.isEmpty()) {
        println("Error: ID and Name cannot be empty.")
        return
    }

    val patron = Patron(id, name)
    if (repository.addPatron(patron)) {
        println("Success: Patron '$name' registered.")
    } else {
        println("Error: Patron with ID $id already exists!")
    }
}

private fun borrowBook(service: LibraryService) {
    println("\n--- Borrow Book ---")
    print("Enter Patron ID: ")
    val patronId = readln().trim()
    print("Enter ISBN: ")
    val isbn = readln().trim()

    val result = service.borrowBook(patronId, isbn)
    when (result) {
        BorrowResult.SUCCESS -> println("Success: Book borrowed!")
        BorrowResult.BOOK_NOT_FOUND -> println("Error: Book with ISBN $isbn not found!")
        BorrowResult.PATRON_NOT_FOUND -> println("Error: Patron with ID $patronId not found!")
        BorrowResult.NOT_AVAILABLE -> println("Error: This book is currently borrowed by someone else.")
        BorrowResult.LIMIT_REACHED -> println("Error: Patron has reached the limit of 3 books.")
    }
}

private fun returnBook(service: LibraryService) {
    println("\n--- Return Book ---")
    print("Enter Patron ID: ")
    val patronId = readln().trim()
    print("Enter ISBN: ")
    val isbn = readln().trim()

    if (service.returnBook(patronId, isbn)) {
        println("Success: Book returned!")
    } else {
        println("Error: Return failed. Ensure ID and ISBN are correct and the book was borrowed.")
    }
}

private fun search(service: LibraryService) {
    print("\nSearch (Title or Author): ")
    val query = readln().trim()
    if (query.isEmpty()) {
        println("Please enter a search term.")
        return
    }
    
    val results = service.search(query)
    if (results.isEmpty()) {
        println("No books found matching '$query'.")
    } else {
        println("\nFound ${results.size} books:")
        results.forEach { println("- ${it.isbn}: ${it.title} by ${it.author} (${it.year}) [${if (it.isAvailable) "Available" else "Borrowed"}]") }
    }
}

private fun listAllBooks(repository: LibraryRepository) {
    val books = repository.getAllBooks()
    if (books.isEmpty()) {
        println("\nThe library is empty.")
    } else {
        println("\nLibrary Catalog (${books.size} books):")
        books.forEach { println("- ${it.isbn}: ${it.title} by ${it.author} (${it.year}) [${if (it.isAvailable) "Available" else "Borrowed"}]") }
    }
}

private fun listAllPatrons(repository: LibraryRepository) {
    val patrons = repository.getAllPatrons()
    if (patrons.isEmpty()) {
        println("\nNo patrons registered.")
    } else {
        println("\nRegistered Patrons (${patrons.size}):")
        patrons.forEach { patron ->
            println("- ${patron.id}: ${patron.name} (Borrowed ISBNs: ${patron.borrowedBooks.joinToString(", ").ifEmpty { "None" }})")
        }
    }
}