declare type Task = {
  id: number
  title: string
  content: string
  deadline: string
  budget: number
  status: string
}

declare type NewTask = {
  title: string
  content: string
  deadline: string
  budget: number
  roomId: number
  userId: number
}