declare type Task = {
  id: number
  title: string
  content: string
  deadline: string
  budget: number
  status: string
  author: string
  authorUserId: string
  assignee: string | null
  assigneeUserId: string | null
}

declare type ApiNewTask = {
  title: string
  content: string
  deadline: string
  budget: number
  roomId: string
  userId: string
}

declare type TNewTask = {
  title: string
  content: string
  deadline: Date
  budget: number
  roomId: string
  userId: string
}

declare type FieldError = {
  fieldName: String
  message: String
}
