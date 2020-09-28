package cash.just.support

data class SupportResponse(val version:Int, val language:String, val categories: List<Category>, val pages:List<Page>)

data class Category(val id:String, val title:String)

data class Page(val title:String, val content:String)